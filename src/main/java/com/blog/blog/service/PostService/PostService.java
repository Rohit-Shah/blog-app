package com.blog.blog.service.PostService;

import com.blog.blog.DTO.PostRequest.PostDTO;
import com.blog.blog.Exceptions.PostNotFoundException;
import com.blog.blog.entity.PostEntity.Post;
import com.blog.blog.entity.UserEntity.User;
import com.blog.blog.entity.UserEntity.UserPrincipal;
import com.blog.blog.repository.PostRepository.PostRepository;
import com.blog.blog.repository.UserRepository.UserRepository;
import com.blog.blog.service.FileService.FileService;
import com.blog.blog.service.RedisService.RedisService;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    private RedisService redisService;

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
        List<Post> allUserPostsFromDB = postRepository.findPostByAuthorId(user.getUserId());
        //System.out.println(allUserPostsFromDB.get(0).getPostId());
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
        if(!dbPost.getAuthorId().equals(user.getUserId())){
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
        if(!dbPost.getAuthorId().equals(user.getUserId())){
            throw new RuntimeException("You are not authorized to delete this post");
        }
        postRepository.delete(dbPost);
        String message = "Post Deleted";
        return message;
    }

    @Transactional
    public Map<String,Object> getAllPosts(UserPrincipal userPrincipal,int page,int size) {
        User user = userPrincipal.getUser();
        if(user == null){
            throw new UsernameNotFoundException("Please login to view the page");
        }
        Map<String,Object> response = new HashMap<>();
        Pageable pageable = PageRequest.of(page-1,size);
        TypeReference<List<PostDTO>> typeRef = new TypeReference<List<PostDTO>>() {};
        if(redisService.get("posts_till_page_" + page + "_" + size,typeRef) != null){
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
            redisService.set("posts_till_page_"+page+"_"+size,postDTOList,200l);
            redisService.set("totalPostPages",pagePosts.getTotalPages(),200l);
            response.put("posts",postDTOList);
            response.put("pages",pagePosts.getTotalPages());
            return response;
        }
    }

    private Post convertPostDTOToEntity(PostDTO postDTO,User user){
        Post post = new Post();
        post.setTitle(postDTO.getTitle());
        post.setContent(postDTO.getContent());
        post.setAuthorId(user.getUserId());
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

        return postDTO;
    }
}
