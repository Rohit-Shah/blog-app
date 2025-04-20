package com.blog.blog.DTO;

import lombok.Data;

@Data
public class PostReactionDTO {
    private String reactionType;
    private long likeCount;
    private long dislikeCount;
}
