package com.blog.blog.service.AuthService;

import com.blog.blog.Response.AuthResponse;

public interface OAuthProviderService {
    AuthResponse authenticate(String authCode);
}
