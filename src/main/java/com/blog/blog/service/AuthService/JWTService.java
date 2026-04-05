package com.blog.blog.service.AuthService;

import com.blog.blog.entity.UserEntity.Role;
import com.blog.blog.entity.UserEntity.User;

import java.util.Date;
import java.util.List;

public interface JWTService {
    String generateAccessToken(User user, Date expirationTime);
    String generateRefreshToken(User user, Date expirationTime);
    boolean validateToken(String token,String tokenType);
    String extractUsername(String token);
    Long extractUserId(String token);
    String extractTokenType(String token);
    int extractTokenVersion(String token);
    List<String> extractRoles(String token);
}
