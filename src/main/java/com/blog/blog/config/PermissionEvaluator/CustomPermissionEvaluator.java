package com.blog.blog.config.PermissionEvaluator;

import com.blog.blog.config.PermissionEvaluator.CommentPermissionEvaluator.CommentPermissionEvaluator;
import com.blog.blog.config.PermissionEvaluator.PostPermissionEvaluator.PostPermissionEvaluator;
import com.blog.blog.constants.PermissionContants.PermissionConstants;
import com.blog.blog.entity.UserEntity.UserPrincipal;
import com.blog.blog.repository.PostRepository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomPermissionEvaluator implements PermissionEvaluator {

    private final PostPermissionEvaluator postPermissionEvaluator;
    private final CommentPermissionEvaluator commentPermissionEvaluator;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        if(authentication == null || permission == null){
            return false;
        }
        Long userId = extractUserId(authentication);
        if(!(targetId instanceof Long id)) return false;

        return switch (permission.toString()) {
            case PermissionConstants.CAN_EDIT_OWN_POST -> postPermissionEvaluator.canEdit(userId, id);
            case PermissionConstants.CAN_DELETE_OWN_POST -> postPermissionEvaluator.canDelete(userId, id);
            case PermissionConstants.CAN_EDIT_OWN_COMMENT -> commentPermissionEvaluator.canEdit(userId, id);
            case PermissionConstants.CAN_DELETE_OWN_COMMENT -> commentPermissionEvaluator.canDelete(userId, id);
            default -> false;
        };
    }

    private Long extractUserId(Authentication authentication){
        return ((UserPrincipal)authentication.getPrincipal()).getUser().getUserId();
    }


}
