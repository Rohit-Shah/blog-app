package com.blog.blog.DTO.PostRequest;

import com.blog.blog.DTO.UserRequest.UserDTO;
import com.blog.blog.constants.PostContants.PostCategory;
import com.blog.blog.constants.PostContants.PostStatus;
import lombok.Data;

import java.time.Instant;

@Data
public class PostDTO {
    private Long postId;
    private String title;
    private String content;
    private String excerpt;
    private String imageUrl;
    private PostStatus postStatus;
    private PostCategory category;
    private String metaDescription;
    private UserDTO user;
}
