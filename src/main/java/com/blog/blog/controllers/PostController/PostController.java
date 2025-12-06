package com.blog.blog.controllers.PostController;

import com.blog.blog.DTO.PostRequest.PostDTO;
import com.blog.blog.Exceptions.PostNotFoundException;
import com.blog.blog.Request.PaginationRequest;
import com.blog.blog.Response.ApiResponse;
import com.blog.blog.entity.UserEntity.UserPrincipal;
import com.blog.blog.service.FileService.FileService;
import com.blog.blog.service.PostService.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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
            CompletableFuture<PostDTO> savedPost = postService.savePost(userPrincipal,postRequest,file);
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
    public ResponseEntity<ApiResponse> getAllUserPosts(@AuthenticationPrincipal UserPrincipal userPrincipal,int page,int size){
        try{
            Map<String, Object> allUserPosts = postService.getAllUserPosts(userPrincipal, page, size);
            ApiResponse successResponse = new ApiResponse("All user posts",true,allUserPosts);
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
    public ResponseEntity<ApiResponse> updatePostByPostId(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long postId,@ModelAttribute PostDTO newPostRequest,@RequestParam(value = "file",required = false)MultipartFile file){
        try{
            CompletableFuture<PostDTO> updatedPost = postService.updatePostByPostId(userPrincipal,postId,newPostRequest,file);
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
                                                   @RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "10") int size){
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

    @PostMapping("/{postId}/bookmark")
    public ResponseEntity<ApiResponse> bookmarkPost(@AuthenticationPrincipal UserPrincipal userPrincipal,@PathVariable Long postId){
        try{
            String isPostBookmarked = postService.bookmarkPost(userPrincipal, postId);
            ApiResponse successResponse = new ApiResponse("Post Bookmarked",true,null);
            return ResponseEntity.status(HttpStatus.OK).body(successResponse);
        }catch (Exception e){
            ApiResponse errorResponse = new ApiResponse(e.getMessage(),false,null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/get-all-bookmarked-posts")
    public ResponseEntity<ApiResponse> getAllBookmarkedPosts(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                             @PageableDefault (page = 0,size = 10,sort = "id",direction = Sort.Direction.DESC) Pageable pageable){
        try{
            String sortField = "id";
            Sort.Direction direction = Sort.Direction.DESC;
            if(pageable.getSort().isSorted()){
                Sort.Order order = pageable.getSort().iterator().next();
                sortField = order.getProperty();
                direction = order.getDirection();
            }
            PaginationRequest paginationRequest = new PaginationRequest(pageable.getPageNumber(),pageable.getPageSize(),sortField,direction);
            List<PostDTO> allBookmarkedPosts = postService.getAllBookmarkedPosts(userPrincipal, paginationRequest);
            ApiResponse successResponse = new ApiResponse("All bookmarked posts ",true,allBookmarkedPosts);
            return ResponseEntity.status(HttpStatus.OK).body(successResponse);
        }catch (Exception e){
            ApiResponse errorResponse = new ApiResponse(e.getMessage(),false,null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/get-profile-posts")
    public ResponseEntity<ApiResponse> getRecentPosts(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                      @PageableDefault (page = 0,size = 10,sort = "id",direction = Sort.Direction.DESC) Pageable pageable){
        //all profile posts consists of recent posts and popular posts
        Map<String,List<PostDTO>> allProfilePosts = null;
        try {
            allProfilePosts = postService.getAllProfilePosts(userPrincipal,pageable);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        ApiResponse successResponse = new ApiResponse("All profile posts ",true,allProfilePosts);
        return ResponseEntity.status(HttpStatus.OK).body(successResponse);
    }

}
