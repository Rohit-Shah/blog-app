package com.blog.blog.Response;

import com.blog.blog.DTO.UserRequest.UserDTO;
import lombok.Data;

@Data
public class AuthResponse {
    private UserDTO user;
    private String accessToken;
    private String refreshToken;
}
