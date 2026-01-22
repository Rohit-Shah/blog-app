package com.blog.blog.config;

import com.blog.blog.entity.PostEntity.Post;
import com.blog.blog.entity.UserEntity.UserPrincipal;
import com.blog.blog.repository.PostRepository.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
@Slf4j
public class CustomPermissionEvaluator implements PermissionEvaluator {

    @Autowired
    private PostRepository postRepository;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        if(authentication == null || permission == null){
            return false;
        }
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Long userId = userPrincipal.getUser().getUserId();
        if("Post".equals(targetType)){
            try{
                Long postId = (Long) targetId;
                return postRepository.existsByPostIdAndUserId(postId,userId) != null;
            }catch (Exception e){
                log.debug("Error while updating post - {} ",e.getMessage());
                return false;
            }
        }
        return false;
    }
}
