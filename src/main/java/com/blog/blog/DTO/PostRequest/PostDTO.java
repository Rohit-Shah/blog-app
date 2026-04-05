package com.blog.blog.DTO.PostRequest;

import com.blog.blog.constants.PostContants.PostStatus;
import lombok.Data;

import java.time.Instant;

@Data
public class PostDTO {
    private String postId;
    private String title;
    private String content;
    private String excerpt;
    private String imageUrl;
    private PostStatus postStatus;
    private String category;
    private String metaDescription;
    private String userId;
    private String userName;
}
