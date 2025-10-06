package com.blog.blog.DTO.PostRequest;

import lombok.Data;

@Data
public class PostReactionDTO {
    private String reactionType;
    private long likeCount;
    private long dislikeCount;
}
