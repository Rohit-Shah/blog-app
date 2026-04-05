package com.blog.blog.controllers.PostController;

import com.blog.blog.DTO.PostRequest.PostDTO;
import com.blog.blog.Exceptions.PostExceptions.PostNotFoundException;
import com.blog.blog.Response.ApiResponse;
import com.blog.blog.entity.UserEntity.UserPrincipal;
import com.blog.blog.service.FileService.FileService;
import com.blog.blog.service.PostService.PostService;
import com.blog.blog.service.serviceBean.PostServiceBean.PostServiceBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;


@RestController
@RequestMapping("/post")
public class PostController {

    private final PostService postService;
    private final FileService fileService;

    public PostController(PostServiceBean postService, @Qualifier("cloudinaryFileService")FileService fileService){
        this.postService = postService;
        this.fileService = fileService;
    }

    @PostMapping("/add-post")
    public ResponseEntity<ApiResponse> addPost(@AuthenticationPrincipal UserPrincipal userPrincipal, @ModelAttribute PostDTO postRequest, @RequestParam(value = "file",required = false)MultipartFile file){
        String imageUrl = fileService.uploadFile(file);
        PostDTO savedPost = postService.addPost(userPrincipal,postRequest,imageUrl);
        ApiResponse successResponse = new ApiResponse("Post Added",true,savedPost);
        return ResponseEntity.status(HttpStatus.OK).body(successResponse);
    }

//    @GetMapping("/get-all-user-posts")
//    public ResponseEntity<ApiResponse> getAllUserPosts(@AuthenticationPrincipal UserPrincipal userPrincipal,int page,int size){
//        try{
//            Map<String, Object> allUserPosts = postService.getAllUserPosts(userPrincipal, page, size);
//            ApiResponse successResponse = new ApiResponse("All user posts",true,allUserPosts);
//            return ResponseEntity.status(HttpStatus.OK).body(successResponse);
//        }catch (UsernameNotFoundException e){
//            ApiResponse errorResponse = new ApiResponse(e.getMessage(),false,null);
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
//        } catch (Exception e){
//            ApiResponse errorResponse = new ApiResponse("Some error occurred",false,null);
//            return ResponseEntity.status(HttpStatus.INTERNsAL_SERVER_ERROR).body(errorResponse);
//        }
//    }

    @GetMapping("/get-post/{postId}")
    public ResponseEntity<ApiResponse> getPostByPostId(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long postId){
        try{
            PostDTO userPost = postService.getPostById(postId);
            ApiResponse successResponse = new ApiResponse("Your post",true,userPost);
            return ResponseEntity.status(HttpStatus.OK).body(successResponse);
        }catch (PostNotFoundException e){
            ApiResponse errorResponse = new ApiResponse(e.getMessage(),false,null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e){
            ApiResponse errorResponse = new ApiResponse("Some error occurred",false,null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PutMapping("/update-post/{postId}")
    public ResponseEntity<ApiResponse> updatePostByPostId(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long postId, @ModelAttribute PostDTO newPostRequest, @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {
        String imageUrl = fileService.uploadFile(null);
        PostDTO updatedPost = postService.updatePost(userPrincipal, postId, newPostRequest, imageUrl);
        ApiResponse successResponse = new ApiResponse("Post updated successfully", true, updatedPost);
        return ResponseEntity.ok(successResponse);
    }


    @DeleteMapping("/delete-post/{postId}")
    public ResponseEntity<ApiResponse> deletePostByPostId(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long postId){
        postService.deletePost(userPrincipal,postId);
        ApiResponse successResponse = new ApiResponse("Post deleted successfully",true,null);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(successResponse);

    }

    @GetMapping("/get-all-posts")
    public ResponseEntity<ApiResponse> getAllPosts(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                   @RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "10") int size){
        try{
            Page<PostDTO> allPosts = postService.getAllPosts(page,size);
            ApiResponse successResponse = new ApiResponse("All Posts",true,allPosts);
            return ResponseEntity.status(HttpStatus.OK).body(successResponse);
        }catch (UsernameNotFoundException e){
            ApiResponse errorResponse = new ApiResponse(e.getMessage(),false,null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e){
            ApiResponse errorResponse = new ApiResponse(e.getMessage(),false,null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

}
