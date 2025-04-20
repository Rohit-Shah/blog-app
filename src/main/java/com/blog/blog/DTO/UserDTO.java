package com.blog.blog.DTO;

import com.blog.blog.entity.User;
import lombok.Data;

import java.util.List;

@Data
public class UserDTO {
    private String id;
    private String username;
    private String password;
    private String email;
    private List<String> roles;
    private String accessToken;
    private String refreshToken;
}
