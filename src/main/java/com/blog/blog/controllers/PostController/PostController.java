package com.blog.blog.controllers.PostController;

import com.blog.blog.DTO.PostRequest.PostDTO;
import com.blog.blog.DTO.PostRequest.PostSearchCriteria;
import com.blog.blog.Response.ApiResponse;
import com.blog.blog.Response.CursorResponse;
import com.blog.blog.Response.PageResponse;
import com.blog.blog.entity.UserEntity.UserPrincipal;
import com.blog.blog.service.FileService.FileService;
import com.blog.blog.service.PostService.PostService;
import com.blog.blog.service.serviceBean.FileServiceBean.CloudinaryFileService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;


@RestController
@RequestMapping("/post")
public class PostController {

    private final PostService postService;
    private final CloudinaryFileService cloudinaryFileService;

    public PostController(PostService postService, CloudinaryFileService cloudinaryFileService){
        this.postService = postService;
        this.cloudinaryFileService = cloudinaryFileService;
    }

    @PostMapping("/add-post")
    public ResponseEntity<ApiResponse> addPost(@AuthenticationPrincipal UserPrincipal userPrincipal, @ModelAttribute PostDTO postRequest, @RequestParam(value = "file",required = false)MultipartFile file){
        String imageUrl = file != null ? cloudinaryFileService.uploadFile(file) : null;
        PostDTO savedPost = postService.addPost(userPrincipal,postRequest,imageUrl);
        ApiResponse successResponse = new ApiResponse("Post Added",true,savedPost);
        return ResponseEntity.status(HttpStatus.OK).body(successResponse);
    }


    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse> getPostByPostId(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long postId){
        PostDTO userPost = postService.getPostById(postId);
        ApiResponse successResponse = new ApiResponse("Your post",true,userPost);
        return ResponseEntity.status(HttpStatus.OK).body(successResponse);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<ApiResponse> updatePostByPostId(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long postId, @ModelAttribute PostDTO newPostRequest, @RequestParam(value = "file", required = false) MultipartFile file)  {
        String imageUrl = file != null ? cloudinaryFileService.uploadFile(file) : null;
        PostDTO updatedPost = postService.updatePost(userPrincipal, postId, newPostRequest, imageUrl);
        ApiResponse successResponse = new ApiResponse("Post updated successfully", true, updatedPost);
        return ResponseEntity.ok(successResponse);
    }


    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse> deletePostByPostId(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long postId){
        postService.deletePost(userPrincipal,postId);
        ApiResponse successResponse = new ApiResponse("Post deleted successfully",true,null);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(successResponse);
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getAllPosts(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "10") int size,
                                                   @ModelAttribute PostSearchCriteria criteria){
        CursorResponse<PostDTO> allPosts = postService.getAllPosts(size,criteria);
        ApiResponse successResponse = new ApiResponse("All Posts",true,allPosts);
        return ResponseEntity.status(HttpStatus.OK).body(successResponse);
    }

}
