package com.blog.blog.config.PermissionEvaluator.PostPermissionEvaluator;

import com.blog.blog.repository.PostRepository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostPermissionEvaluator {

    private final PostRepository postRepository;

    public boolean canEdit(Long userId,Long postId){
        return postRepository.findUserPostByPostId(userId,postId) != null;
    }

    public boolean canDelete(Long userId, Long postId) {
        return postRepository.findUserPostByPostId(userId,postId) != null;
    }
}
