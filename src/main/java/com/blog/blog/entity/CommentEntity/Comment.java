package com.blog.blog.entity.CommentEntity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Data
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;
    private String content;
    private Long authorId;
    private String authorName;
    private Long postId;
    @CreationTimestamp
    private Instant createdAt;
}

