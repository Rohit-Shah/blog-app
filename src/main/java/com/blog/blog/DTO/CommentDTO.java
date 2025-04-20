package com.blog.blog.DTO;

import com.blog.blog.entity.User;
import lombok.Data;

@Data
public class CommentDTO {
    private Long commentId;
    private String content;
    private String authorName;
    private String authorId;
    private Long postId;
}
