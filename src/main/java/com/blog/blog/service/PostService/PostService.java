package com.blog.blog.service.PostService;

import com.blog.blog.DTO.PostRequest.PostDTO;
import com.blog.blog.Exceptions.PostNotFoundException;
import com.blog.blog.Request.PaginationRequest;
import com.blog.blog.entity.PostEntity.Bookmark;
import com.blog.blog.entity.PostEntity.Post;
import com.blog.blog.entity.PostEntity.PostReaction;
import com.blog.blog.entity.UserEntity.User;
import com.blog.blog.entity.UserEntity.UserPrincipal;
import com.blog.blog.repository.PostRepository.BookmarkRepository;
import com.blog.blog.repository.PostRepository.PostReactionRepository;
import com.blog.blog.repository.PostRepository.PostRepository;
import com.blog.blog.repository.UserRepository.UserRepository;
import com.blog.blog.service.FileService.FileService;
import com.blog.blog.service.RedisService.RedisService;
import com.blog.blog.utility.PageRequestUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.support.PageableUtils;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileService fileService;

    @Autowired
    private PostReactionRepository postReactionRepository;

    @Autowired
    private RedisService redisService;

    @Autowired
    private BookmarkRepository bookmarkRepository;

//    private static final Logger logger = LoggerFactory.getLogger(PostService.class);

    @Transactional
    public PostDTO savePost(@AuthenticationPrincipal UserPrincipal userPrincipal, PostDTO postRequest, MultipartFile file) {
        User user = userPrincipal.getUser();
        if(user == null){
            throw new UsernameNotFoundException("No such user found");
        }
        postRequest.setAuthorId(user.getUserId().toString());
        String imageUrl = null;
        if(file != null){
            imageUrl = fileService.uploadFile(file);
        }
        postRequest.setImageUrl(imageUrl);
        Post userPost = convertPostDTOToEntity(postRequest,user);
        Post savedPost = postRepository.save(userPost);
        //reset the redis cache
        List<String> keysToResetFromRedis = new ArrayList<>();
        keysToResetFromRedis.add("posts_till_page_*");
        keysToResetFromRedis.add("totalPostPages");
        redisService.resetCacheOfKeys(keysToResetFromRedis);
        return convertPostEntityToDTO(savedPost,user);
    }

    public List<PostDTO> getAllUserPosts(UserPrincipal userPrincipal) {
        List<PostDTO> allUserPosts = new ArrayList<>();
        User user = userPrincipal.getUser();
        List<Post> allUserPostsFromDB = postRepository.findPostByUserId(user.getUserId());
        return allUserPostsFromDB.stream().map(post -> convertPostEntityToDTO(post,user)).collect(Collectors.toList());
    }

    public PostDTO getPostByPostId(UserPrincipal userPrincipal, Long postId) {
        User user = userPrincipal.getUser();
        Optional<Post> userPost = postRepository.findPostByPostId(postId);
        if(userPost.isEmpty()){
            throw new PostNotFoundException("No such post found");
        }
        return convertPostEntityToDTO(userPost.get(),user);
    }

    public PostDTO updatePostByPostId(UserPrincipal userPrincipal, Long postId,PostDTO updatedPost) {
        //check if the given post belong to the logged-in user or not
        User user = userPrincipal.getUser();
        Optional<Post> currPost = postRepository.findPostByPostId(postId);
        if(currPost.isEmpty()){
            throw new PostNotFoundException("No such post found");
        }
        Post dbPost = currPost.get();
        if(!dbPost.getUserId().equals(user.getUserId())){
            throw new RuntimeException("You are not authorized to edit this post");
        }
        dbPost.setTitle(updatedPost.getTitle().isEmpty() ? dbPost.getTitle() : updatedPost.getTitle());
        dbPost.setContent(updatedPost.getContent().isEmpty() ? dbPost.getContent() : updatedPost.getContent());
        Post updatedSavedPost = postRepository.save(dbPost);
        return convertPostEntityToDTO(updatedSavedPost,user);
    }

    public String deletePostByPostId(UserPrincipal userPrincipal, Long postId) {
        User user = userPrincipal.getUser();
        Optional<Post> currPost = postRepository.findPostByPostId(postId);
        if(currPost.isEmpty()){
            throw new PostNotFoundException("No such post found");
        }
        Post dbPost = currPost.get();
        if(!dbPost.getUserId().equals(user.getUserId())){
            throw new RuntimeException("You are not authorized to delete this post");
        }
        postRepository.delete(dbPost);
        String message = "Post Deleted";
        return message;
    }

    @Transactional
    public Map<String,Object> getAllPosts(UserPrincipal userPrincipal,int page,int size) {
        User user = userPrincipal.getUser();
        Map<String,Object> response = new HashMap<>();
        Pageable pageable = PageRequest.of(page-1,size);
        TypeReference<List<PostDTO>> typeRef = new TypeReference<List<PostDTO>>() {};
        if(redisService.get(user.getUserId() + "posts_till_page_" + page + "_" + size,typeRef) != null){
            List<PostDTO> postDTOList = redisService.get("posts_till_page_" + page + "_" + size, new TypeReference<>() {
            });
            int totalPages = redisService.get("totalPostPages", new TypeReference<Integer>() {});
            response.put("posts",postDTOList);
            response.put("pages",totalPages);
            return response;
        }
        else{
            Page<Post> pagePosts = postRepository.findAll(pageable);
            List<PostDTO> postDTOList = pagePosts.stream().map(post -> convertPostEntityToDTO(post, user)).toList();
            redisService.set(user.getUserId() + "posts_till_page_"+page+"_"+size,postDTOList,200l);
            redisService.set(user.getUserId() + "totalPostPages",pagePosts.getTotalPages(),200l);
            response.put("posts",postDTOList);
            response.put("pages",pagePosts.getTotalPages());
            return response;
        }
    }

    private Post convertPostDTOToEntity(PostDTO postDTO,User user){
        Post post = new Post();
        post.setTitle(postDTO.getTitle());
        post.setContent(postDTO.getContent());
        post.setUserId(user.getUserId());
        post.setImageUrl(postDTO.getImageUrl());
        return post;
    }

    private PostDTO convertPostEntityToDTO(Post post,User user){
        PostDTO postDTO = new PostDTO();
        postDTO.setPostId(post.getPostId().toString());
        postDTO.setTitle(post.getTitle());
        postDTO.setContent(post.getContent());
        postDTO.setAuthorId(user.getUserId().toString());
        postDTO.setAuthorName(user.getUsername());
        postDTO.setCreatedAt(post.getCreatedAt());
        postDTO.setUpdatedAt(post.getUpdatedAt());
        postDTO.setImageUrl(post.getImageUrl());
        //get like and dislike count
        long postLikeCount = postReactionRepository.countPostLikes(post.getPostId());
        long postDislikeCount = postReactionRepository.countPostDislikes(post.getPostId());
        postDTO.setLikeCount(postLikeCount);
        postDTO.setDislikeCount(postDislikeCount);

        //check if user has reacted to this post or not
        Optional<PostReaction.ReactionType> userPostReactionOptional = postReactionRepository.findUserReaction(user.getUserId(),post.getPostId());
        if(userPostReactionOptional.isEmpty()){
            postDTO.setLikedByUser(false);
            postDTO.setDislikedByUser(false);
        }
        else{
            PostReaction.ReactionType userPostReactionType = userPostReactionOptional.get();
            if(userPostReactionType.toString().equals("LIKE")){
                postDTO.setLikedByUser(true);
                postDTO.setDislikedByUser(false);
            }
            else{
                postDTO.setLikedByUser(false);
                postDTO.setDislikedByUser(true);
            }
        }
        return postDTO;
    }

    public String bookmarkPost(UserPrincipal userPrincipal, Long postId) {
        User currUser = userPrincipal.getUser();
        Post post = postRepository.findPostByPostId(postId).orElseThrow(() -> new PostNotFoundException("No such post found"));
        Bookmark newBookmark = new Bookmark();
        newBookmark.setPost(post);
        newBookmark.setUser(currUser);
        bookmarkRepository.save(newBookmark);
        return "Post bookmarked successfully";
    }

    public List<PostDTO> getAllBookmarkedPosts(UserPrincipal userPrincipal, PaginationRequest paginationRequest) {
        User user = userPrincipal.getUser();
        final Pageable pageable = PageRequestUtil.getPageableRequest(paginationRequest);
        final Page<Post> postPages = bookmarkRepository.findBookmarkedPostByUser(user,pageable);
        List<PostDTO> allBookmarkedPostDTOList = postPages.stream()
                .map((post) -> {
                    return convertPostEntityToDTO(post,user);
                } ).toList();
        return allBookmarkedPostDTOList;
    }
}
