package com.blog.blog.Response;

import lombok.Data;

@Data
public class AuthResponse {
    private String username;
    private String email;
    private String accessToken;
    private String refreshToken;
}
