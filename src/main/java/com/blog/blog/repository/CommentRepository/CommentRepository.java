package com.blog.blog.repository.CommentRepository;

import com.blog.blog.entity.CommentEntity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findCommentsByPostId(Long postId);
}
