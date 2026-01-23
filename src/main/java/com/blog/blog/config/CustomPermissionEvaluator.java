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
        String perm = permission.toString();
        switch (perm){
            case "CAN_EDIT_OWN_POST","CAN_DELETE_OWN_POST":
                return handlePostOwnership(targetId,userId);
            case "CAN_EDIT_OWN_COMMENT","CAN_MODERATE_COMMENT":
                return handleCommentOwnership(targetId,userId);
            default:
                return false;
        }

    }

    private boolean handlePostOwnership(Serializable targetId,Long userId){
        if(!(targetId instanceof Long postId)){
            return false;
        }
        try{
            postId = (Long) targetId;
            return postRepository.existsByPostIdAndUserId(postId,userId) != null;
        }catch (Exception e){
            log.debug("Error while handling post ownership for post id {} and user id {} ",postId,userId);
            return false;
        }
    }

    private boolean handleCommentOwnership(Serializable targetId,Long userId){
        return true;
    }
}
