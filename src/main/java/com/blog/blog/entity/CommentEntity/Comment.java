package com.blog.blog.entity.CommentEntity;

import com.blog.blog.entity.AuditEntity.AuditEntity;
import com.blog.blog.entity.PostEntity.Post;
import com.blog.blog.entity.UserEntity.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(
        name = "comments",
        indexes = {
            @Index(name = "idx_comment_post",columnList = "post_id"),
            @Index(name = "idx_parent_comment",columnList = "parent_comment_id")
        }

)
public class Comment extends AuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;
    @Column(nullable = false,length = 1000)
    private String content;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id",nullable = false)
    private Post post;
    @Column(name = "parent_comment_id")
    private Long parentCommentId;
    @Column(nullable = false)
    private boolean deleted = false;
}

