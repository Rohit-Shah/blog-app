package com.blog.blog.controllers.CommentController;

import com.blog.blog.DTO.CommentReqeust.CommentDTO;
import com.blog.blog.Exceptions.CommentNotFoundException;
import com.blog.blog.Exceptions.PostExceptions.PostNotFoundException;
import com.blog.blog.Response.ApiResponse;
import com.blog.blog.entity.UserEntity.UserPrincipal;
import com.blog.blog.service.serviceBean.CommentServiceBean.CommentServiceBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comment")
@Slf4j
public class CommentController {
    @Autowired
    private CommentServiceBean commentService;

    @PostMapping("/add-comment/{postId}")
    public ResponseEntity<ApiResponse> addCommentOnPost(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                        @RequestBody CommentDTO commentDTO,
                                                        @PathVariable Long postId) {
        CommentDTO commentResponse = commentService.addCommentOnPost(userPrincipal, commentDTO, postId);
        ApiResponse successResponse = new ApiResponse("Comment added", true, commentResponse);
        return ResponseEntity.status(HttpStatus.OK).body(successResponse);
    }

    @GetMapping("/get-comments/{postId}")
    public ResponseEntity<ApiResponse> getAllPostComments(int page,int size,@PathVariable Long postId) {
        Page<CommentDTO> commentResponse = commentService.getPostComments(page,size,postId);
        ApiResponse successResponse = new ApiResponse("Post Comments", true, commentResponse);
        return ResponseEntity.status(HttpStatus.OK).body(successResponse);
    }

    @DeleteMapping("/delete-comment/{postId}/{commentId}")
    public ResponseEntity<ApiResponse> deleteCommentOnPost(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                           @PathVariable Long postId,
                                                           @PathVariable Long commentId) {
        commentService.deleteCommentOnPost(userPrincipal, postId, commentId);
        ApiResponse successResponse = new ApiResponse("Comment added", true, null);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(successResponse);
    }
}
