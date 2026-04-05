package com.blog.blog.entity.PostEntity;

import com.blog.blog.constants.PostContants.PostCategory;
import com.blog.blog.constants.PostContants.PostStatus;
import com.blog.blog.entity.AuditEntity.AuditEntity;
import com.blog.blog.entity.UserEntity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "posts",
    indexes = {
        @Index(name = "idx_post_user",columnList = "user_id"),
        @Index(name = "idx_post_feed", columnList = "status,deleted,publishedAt"),
        @Index(name = "idx_post_category", columnList = "category")
    }
)
public class Post extends AuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;
    @Column(nullable = false,length = 100)
    private String title;
    @Column(nullable = false,columnDefinition = "TEXT")
    private String content;
    @Column(nullable = false,unique = true,updatable = false)
    private String slug;
    private String imageUrl;
    @Column(nullable = false)
    private boolean deleted = false;
    private Date publishedAt;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PostStatus status;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length = 10)
    private PostCategory category;
    private Long views;
    @Column(length = 300)
    private String metaDescription;
    @Version
    private Integer version;
}
