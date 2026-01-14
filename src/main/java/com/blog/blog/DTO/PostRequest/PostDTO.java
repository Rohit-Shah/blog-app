package com.blog.blog.DTO.PostRequest;

import lombok.Data;

import java.time.Instant;

@Data
public class PostDTO {
    private String postId;
    private String title;
    private String excerpt;
    private String content;
    private String imageUrl;
    private String authorId;
    private String authorName;
    private Instant createdAt;
    private Instant updatedAt;
    private Long likeCount;
    private Long dislikeCount;
    private boolean likedByUser;
    private boolean dislikedByUser;
    private Long viewCount;
}
