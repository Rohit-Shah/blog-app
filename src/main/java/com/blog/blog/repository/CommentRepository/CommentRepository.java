package com.blog.blog.repository.CommentRepository;

import com.blog.blog.entity.CommentEntity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long>, JpaSpecificationExecutor<Comment> {
    @Modifying
    @Query("UPDATE Comment c SET c.deleted = true where c.commentId = :commentId")
    void deleteComment(Long commentId);

    @Query("SELECT c FROM Comment c where c.user.userId = :userId and c.commentId = :commentId")
    Comment findUserCommentByCommentId(Long userId, Long commentId);
    //Page<Comment> findByPostIdAndParentCommentIsNull(Long postId,Pageable commentPageRequest);
}
