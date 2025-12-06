package com.blog.blog.DTO.UserRequest;

import lombok.Data;

import java.util.List;

@Data
public class UserDTO {
    private Long userId;
    private String username;
    private String email;
    private Long totalPosts;
    private Long totalFollowers;
    private Long totalFollowing;
}
