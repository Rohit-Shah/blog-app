package com.blog.blog.DTO.UserRequest;

import lombok.Data;

@Data
public class UserLoginRequest {
    private String username;
    private String password;
}
