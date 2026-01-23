package com.blog.blog.service.PostService;

import com.blog.blog.DTO.PostRequest.PostDTO;
import com.blog.blog.Exceptions.PostNotFoundException;
import com.blog.blog.Request.PaginationRequest;
import com.blog.blog.annotations.LogUserAction;
import com.blog.blog.config.AppProperties;
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
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
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
    @Autowired
    private AppProperties appProperties;

    @LogUserAction(actionType = "CREATE_POST")
    public CompletableFuture<PostDTO> savePost(@AuthenticationPrincipal UserPrincipal userPrincipal, PostDTO postRequest, MultipartFile file) throws ExecutionException, InterruptedException, IOException {
        User user = userPrincipal.getUser();
        if(user == null){
            throw new UsernameNotFoundException("No such user found");
        }
        Post currPost = convertPostDTOToEntity(postRequest,user);
        //save post
        CompletableFuture<Post> initialSavePostFuture = CompletableFuture.supplyAsync(() -> postRepository.save(currPost));
        //upload file
        CompletableFuture<String> imageUploadFuture = null;
        if(file != null){
            byte[] imageBytes = file.getBytes();
            imageUploadFuture = fileService.uploadFile(imageBytes);
        }
        CompletableFuture<String> safeImageUploadFuture =  imageUploadFuture != null ? imageUploadFuture.exceptionally(ex -> {
            log.error("Error uploading image for user with userId : {} ",user.getUserId());
            return null;
        }) : null;
        //once both are done combine them
        if(safeImageUploadFuture == null){
            return initialSavePostFuture.thenApply(savedPost -> convertPostEntityToDTO(savedPost,user));
        }
        return initialSavePostFuture.thenCombine(safeImageUploadFuture,(savedPost, uploadedImageUrl) -> {
            return updatePostWithImageDetails(savedPost,uploadedImageUrl,user);
        }).thenApply(postDTO -> postDTO);
    }

    @Transactional
    private PostDTO updatePostWithImageDetails(Post savedPost,String imageUrl,User user){
        if(imageUrl != null){
            savedPost.setImageUrl(imageUrl);
            postRepository.save(savedPost);
        }
        return convertPostEntityToDTO(savedPost,user);
    }

    public Map<String,Object> getAllUserPosts(UserPrincipal userPrincipal,int page,int size) {
        User user = userPrincipal.getUser();
        Map<String,Object> response = new HashMap<>();
        Pageable pageable = PageRequest.of(page-1,size);
        TypeReference<List<PostDTO>> typeRef = new TypeReference<List<PostDTO>>() {};
        if(redisService.get(user.getUserId() + "_posts_till_page_" + page + "_" + size,typeRef) != null){
            List<PostDTO> postDTOList = redisService.get(user.getUserId() + "_posts_till_page_" + page + "_" + size, new TypeReference<>() {
            });
            int totalPages = redisService.get(user.getUserId() + "_userTotalPostPages", new TypeReference<Integer>() {});
            response.put("posts",postDTOList);
            response.put("pages",totalPages);
        }
        else{
            Page<Post> pagePosts = postRepository.findAllUserPosts(pageable,user.getUserId());
            List<PostDTO> postDTOList = pagePosts.stream().map(post -> convertPostEntityToDTO(post, user)).toList();
            redisService.set(user.getUserId() + "_posts_till_page_" + page + "_" + size,postDTOList,200l);
            redisService.set(user.getUserId() + "_userTotalPostPages",pagePosts.getTotalPages(),200l);
            response.put("posts",postDTOList);
            response.put("pages",pagePosts.getTotalPages());
        }
        return response;

    }

    @LogUserAction(actionType = "GET_POST")
    public PostDTO getPostByPostId(UserPrincipal userPrincipal, Long postId) {
        User user = userPrincipal.getUser();
        Optional<Post> userPost = postRepository.findPostByPostId(postId);
        updatePostViewsDetailsInRedis(user,postId);
        if(userPost.isEmpty()){
            throw new PostNotFoundException("No such post found");
        }
        return convertPostEntityToDTO(userPost.get(),user);
    }

    private void updatePostViewsDetailsInRedis(User user,Long postId){
        redisService.setPostViewDetails(postId,user.getUserId());
    }

    public void updatePostViewDetailsInDB(Long postId,Long viewCount){
        postRepository.updateViewCountForPost(postId,viewCount);
    }

    @LogUserAction(actionType = "UPDATE_POST")
    @PreAuthorize(
            "hasPermission(#postId,'Post','CAN_EDIT_OWN_POST') or hasAuthority('CAN_EDIT_ANY_POST')"
    )
    public PostDTO updatePostByPostId(UserPrincipal userPrincipal, Long postId, PostDTO updatedPost, MultipartFile file) throws IOException {

        User user = userPrincipal.getUser();

        Post dbPost = postRepository.findPostByPostId(postId)
                .orElseThrow(() -> new PostNotFoundException("No such post found"));

        if (updatedPost.getTitle() != null && !updatedPost.getTitle().isBlank()) {
            dbPost.setTitle(updatedPost.getTitle());
        }

        if (updatedPost.getContent() != null && !updatedPost.getContent().isBlank()) {
            dbPost.setContent(updatedPost.getContent());
        }

        if (file != null && !file.isEmpty()) {
            byte[] imageBytes = file.getBytes();
            try {
                String imageUrl = fileService.uploadFile(imageBytes).join();
                dbPost.setImageUrl(imageUrl);
            } catch (Exception e) {
                log.error("Image upload failed for user {}", user.getUserId());
            }
        }

        Post savedPost = postRepository.save(dbPost);
        return convertPostEntityToDTO(savedPost, user);
    }

    @LogUserAction(actionType = "DELETE_POST")
    @PreAuthorize(
            "hasPermission(#postId,'Post','CAN_DELETE_OWN_POST') or hasAuthority('CAN_DELETE_ANY_POST')"
    )
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

    public Map<String,Object> getAllPosts(UserPrincipal userPrincipal,int page,int size) {
        User user = userPrincipal.getUser();
        Map<String,Object> response = new HashMap<>();
        Pageable pageable = PageRequest.of(page-1,size);
        TypeReference<List<PostDTO>> typeRef = new TypeReference<List<PostDTO>>() {};
        if(redisService.get("posts_till_page_" + page + "_" + size,typeRef) != null){
            List<PostDTO> postDTOList = redisService.get("posts_till_page_" + page + "_" + size, new TypeReference<>() {
            });
            int totalPages = redisService.get("totalPostPages", new TypeReference<Integer>() {});
            response.put("posts",postDTOList);
            response.put("pages",totalPages);
        }
        else{
            Page<Post> pagePosts = postRepository.findAll(pageable);
            List<PostDTO> postDTOList = pagePosts.stream().map(post -> convertPostEntityToDTO(post, user)).toList();
            redisService.set("posts_till_page_" + page + "_" + size,postDTOList,200l);
            redisService.set("totalPostPages",pagePosts.getTotalPages(),200l);
            response.put("posts",postDTOList);
            response.put("pages",pagePosts.getTotalPages());
        }
        return response;
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

    public Map<String,List<PostDTO>> getAllProfilePosts(UserPrincipal userPrincipal,Pageable pageable) throws ExecutionException, InterruptedException {
        //all recent posts
        User currUser = userPrincipal.getUser();
        CompletableFuture<List<PostDTO>> recentPostFuture = this.getAllRecentPosts(currUser,pageable);
        //all popular posts
        CompletableFuture<List<PostDTO>> popularPostFuture = this.getAllPopularPosts(currUser,pageable);

        CompletableFuture.allOf(recentPostFuture,popularPostFuture);
        return Map.of("recentPosts",recentPostFuture.get(),"popularPosts",popularPostFuture.get());
    }

    @Async
    private CompletableFuture<List<PostDTO>> getAllRecentPosts(User user,Pageable pageable) throws ExecutionException,InterruptedException{
        Page<Post> recentPagePosts = postRepository.findPostOrderByCreatedAtDesc(pageable);
        return CompletableFuture.completedFuture(recentPagePosts).thenApply(pagePost -> pagePost.stream().map(post -> this.convertPostEntityToDTO(post,user)).toList());
    }

    @Async
    private CompletableFuture<List<PostDTO>> getAllPopularPosts(User user,Pageable pageable){
        Page<Post> popularPagePosts = postRepository.findPopularPosts(pageable);
        return CompletableFuture.completedFuture(popularPagePosts).thenApply(pagePost -> pagePost.stream().map(post -> this.convertPostEntityToDTO(post,user)).toList());
    }

    public List<PostDTO> getRecentPostsForUsers(Long userId, List<Long> followingIds, Instant cutoff){
        User currUser = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("No user found"));
        int maxCap = appProperties.getMaxCuratedPosts();
        Pageable maxCuratedPostPageable = PageRequest.of(0,Math.min(maxCap, appProperties.getPostsPerFollowing()) * followingIds.size());
        Page<Post> recentsPostPage = postRepository.findRecentPostsForUsers(followingIds,cutoff,maxCuratedPostPageable);
        List<PostDTO> recentUserPostsDTOList = recentsPostPage.stream().map(post -> convertPostEntityToDTO(post,currUser)).collect(Collectors.toList());
        return recentUserPostsDTOList;
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
        postDTO.setViewCount(post.getViewCount());
        //get like and dislike count
        long postLikeCount = postReactionRepository.countPostLikes(post.getPostId());
        long postDislikeCount = postReactionRepository.countPostDislikes(post.getPostId());
        postDTO.setLikeCount(postLikeCount);
        postDTO.setDislikeCount(postDislikeCount);
        postDTO.setExcerpt(getPostExcerpt(post.getContent()));

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

    private static String getPostExcerpt(String message){
        if(message == null) return message;
        return message.length() < 150 ? message :message.substring(0,150) + "...";
    }
}
