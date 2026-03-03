package com.blog.blog.service.TokenService;

import com.blog.blog.Response.AuthResponse;
import com.blog.blog.entity.UserEntity.User;

import java.util.Date;

public interface TokenService {
    AuthResponse rotateRefreshToken(String refreshToken);
    void revokeToken(String refreshToken);
}
