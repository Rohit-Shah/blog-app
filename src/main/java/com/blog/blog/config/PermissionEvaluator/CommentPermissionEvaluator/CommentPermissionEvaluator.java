package com.blog.blog.config.PermissionEvaluator.CommentPermissionEvaluator;

import com.blog.blog.repository.CommentRepository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentPermissionEvaluator {

    private final CommentRepository commentRepository;

    public boolean canEdit(Long userId, Long id) {
        return commentRepository.findUserCommentByCommentId(userId,id) != null;
    }

    public boolean canDelete(Long userId, Long id) {
        return commentRepository.findUserCommentByCommentId(userId,id) != null;
    }
}
