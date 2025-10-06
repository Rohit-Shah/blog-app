package com.blog.blog.controllers.CommentController;

import com.blog.blog.DTO.CommentReqeust.CommentDTO;
import com.blog.blog.Exceptions.CommentNotFoundException;
import com.blog.blog.Exceptions.PostNotFoundException;
import com.blog.blog.Response.ApiResponse;
import com.blog.blog.entity.UserEntity.UserPrincipal;
import com.blog.blog.service.CommentService.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/post/comment")
@Slf4j
public class CommentController {
    @Autowired
    private CommentService commentService;

    @PostMapping("/{postId}/add-comment")
    public ResponseEntity<ApiResponse> addCommentOnPost(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                        @ModelAttribute CommentDTO commentDTO,
                                                        @PathVariable Long postId) {
        try {
            CommentDTO commentResponse = commentService.addComment(userPrincipal, commentDTO, postId);
            ApiResponse successResponse = new ApiResponse("Comment added", true, commentResponse);
            return ResponseEntity.status(HttpStatus.OK).body(successResponse);
        } catch (PostNotFoundException e) {
            ApiResponse errorResponse = new ApiResponse("No such post found", false, null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            ApiResponse errorResponse = new ApiResponse(e.getMessage(), false, null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/{postId}/all-comments")
    public ResponseEntity<ApiResponse> getAllPostComments(@AuthenticationPrincipal UserPrincipal userPrincipal, @ModelAttribute CommentDTO commentDTO, @PathVariable Long postId) {
        try {
            List<CommentDTO> commentResponse = commentService.getAllPostComments(userPrincipal, commentDTO, postId);
            ApiResponse successResponse = new ApiResponse("Comment added", true, commentResponse);
            return ResponseEntity.status(HttpStatus.OK).body(successResponse);
        } catch (PostNotFoundException e) {
            ApiResponse errorResponse = new ApiResponse("No such post found", false, null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            ApiResponse errorResponse = new ApiResponse(e.getMessage(), false, null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @DeleteMapping("{postId}/{commentId}")
    public ResponseEntity<ApiResponse> deleteCommentOnPost(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                           @PathVariable Long postId,
                                                           @PathVariable Long commentId) {
        try {
            CommentDTO commentResponse = commentService.deletePostComment(userPrincipal, postId, commentId);
            ApiResponse successResponse = new ApiResponse("Comment added", true, commentResponse);
            return ResponseEntity.status(HttpStatus.OK).body(successResponse);
        } catch (PostNotFoundException e) {
            ApiResponse errorResponse = new ApiResponse("No such post found", false, null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (CommentNotFoundException e) {
            ApiResponse errorResponse = new ApiResponse("No such comment found", false, null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            ApiResponse errorResponse = new ApiResponse(e.getMessage(), false, null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }

    }
}
