package com.blog.blog.DTO.CommentReqeust;

import lombok.Data;

@Data
public class CommentDTO {
    private Long commentId;
    private String content;
    private String authorName;
    private String authorId;
    private Long postId;
}
