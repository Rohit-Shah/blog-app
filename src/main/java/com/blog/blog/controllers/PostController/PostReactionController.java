package com.blog.blog.controllers.PostController;

import com.blog.blog.DTO.PostRequest.PostReactionDTO;
import com.blog.blog.Exceptions.PostNotFoundException;
import com.blog.blog.Response.ApiResponse;
import com.blog.blog.entity.UserEntity.UserPrincipal;
import com.blog.blog.service.PostService.PostReactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PostReactionController {

    @Autowired
    private PostReactionService postReactionService;

    @PostMapping("/react/{postId}/{reactionType}")
    public ResponseEntity<ApiResponse> likePost(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long postId,@PathVariable String reactionType){
        try{
            PostReactionDTO postDetails = postReactionService.reactToPost(userPrincipal,postId,reactionType);
            ApiResponse successResponse = new ApiResponse("reaction added",true,postDetails);
            return ResponseEntity.status(HttpStatus.OK).body(successResponse);
        }catch (PostNotFoundException e){
            ApiResponse errorResponse = new ApiResponse(e.getMessage(),false,null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e){
            ApiResponse errorResponse = new ApiResponse(e.getMessage(),false,null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
