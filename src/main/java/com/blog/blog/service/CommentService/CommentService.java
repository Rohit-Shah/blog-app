package com.blog.blog.service.CommentService;

import com.blog.blog.DTO.CommentReqeust.CommentDTO;
import com.blog.blog.entity.CommentEntity.Comment;
import com.blog.blog.entity.UserEntity.UserPrincipal;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CommentService {
    //add comment
    CommentDTO addCommentOnPost(UserPrincipal userPrincipal,CommentDTO commentRequest,Long postId);
    //update comment
    CommentDTO updateCommentOnPost(UserPrincipal userPrincipal,CommentDTO commentRequest,Long postId);
    //delete comment
    void deleteCommentOnPost(UserPrincipal userPrincipal,Long postId,Long commentId);
    //get comment
    Page<CommentDTO> getPostComments(int page,int size,Long postId);
    Page<CommentDTO> getCommentReplies(int page,int size,Long commentId);

    CommentDTO getUserCommentByCommentId(Long userId, Long id);
}
