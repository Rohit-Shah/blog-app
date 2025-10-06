package com.blog.blog.controllers.PostController;

import com.blog.blog.DTO.PostRequest.PostDTO;
import com.blog.blog.Exceptions.PostNotFoundException;
import com.blog.blog.Response.ApiResponse;
import com.blog.blog.entity.UserEntity.UserPrincipal;
import com.blog.blog.service.FileService.FileService;
import com.blog.blog.service.PostService.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/post")
public class PostController {

    @Autowired
    private PostService postService;
    @Autowired
    private FileService fileService;

    @PostMapping("/add-post")
    public ResponseEntity<ApiResponse> addPost(@AuthenticationPrincipal UserPrincipal userPrincipal, @ModelAttribute PostDTO postRequest, @RequestParam(value = "file",required = false)MultipartFile file){
        try{
            PostDTO savedPost = postService.savePost(userPrincipal,postRequest,file);
            ApiResponse successResponse = new ApiResponse("Post saved successfully",true,savedPost);
            return ResponseEntity.status(HttpStatus.OK).body(successResponse);
        }catch (UsernameNotFoundException e){
            ApiResponse errorResponse = new ApiResponse(e.getMessage(),false,null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e){
            ApiResponse errorResponse = new ApiResponse(e.getMessage(),false,null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/get-all-user-posts")
    public ResponseEntity<ApiResponse> getAllUserPosts(@AuthenticationPrincipal UserPrincipal userPrincipal){
        try{
            List<PostDTO> userPosts = postService.getAllUserPosts(userPrincipal);
            ApiResponse successResponse = new ApiResponse("All user posts",true,userPosts);
            return ResponseEntity.status(HttpStatus.OK).body(successResponse);
        }catch (UsernameNotFoundException e){
            ApiResponse errorResponse = new ApiResponse(e.getMessage(),false,null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e){
            ApiResponse errorResponse = new ApiResponse("Some error occurred",false,null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/get-post/{postId}")
    public ResponseEntity<ApiResponse> getPostByPostId(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long postId){
        try{
            PostDTO userPost = postService.getPostByPostId(userPrincipal,postId);
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
    public ResponseEntity<ApiResponse> updatePostByPostId(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long postId,@RequestBody PostDTO newPost){
        try{
            PostDTO updatedPost = postService.updatePostByPostId(userPrincipal,postId,newPost);
            ApiResponse successResponse = new ApiResponse("Post updated successfully",true,updatedPost);
            return ResponseEntity.status(HttpStatus.OK).body(successResponse);
        }catch (PostNotFoundException e){
            ApiResponse errorResponse = new ApiResponse(e.getMessage(),false,null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e){
            ApiResponse errorResponse = new ApiResponse(e.getMessage(),false,null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @DeleteMapping("/delete-post/{postId}")
    public ResponseEntity<ApiResponse> deletePostByPostId(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long postId){
        try{
            String deleteMessage = postService.deletePostByPostId(userPrincipal,postId);
            ApiResponse successResponse = new ApiResponse("Post deleted successfully",true,deleteMessage);
            return ResponseEntity.status(HttpStatus.OK).body(successResponse);
        }catch (PostNotFoundException e){
            ApiResponse errorResponse = new ApiResponse(e.getMessage(),false,null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e){
            ApiResponse errorResponse = new ApiResponse("Some error occurred",false,null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/get-all-posts")
    public ResponseEntity<ApiResponse> getAllPosts(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                   @RequestParam(defaultValue = "1") int page,
                                                    @RequestParam(defaultValue = "6") int size){
        try{
            Map<String,Object> allPosts = postService.getAllPosts(userPrincipal,page,size);
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
