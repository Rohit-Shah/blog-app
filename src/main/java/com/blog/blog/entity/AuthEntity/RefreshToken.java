package com.blog.blog.entity.AuthEntity;

import com.blog.blog.entity.UserEntity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.Date;

@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@Table(
        name="refresh_tokens",
        indexes = {
                @Index(name = "idx_refresh_token_user",columnList = "user_id"),
                @Index(name = "idx_refresh_token_token",columnList = "token")
        }
)
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long authId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;
    @Column(nullable = false)
    private String token;
    @Column(nullable = false,updatable = false)
    @CreatedDate
    private Date createdAt;
    @Column(nullable = false)
    private Date expiresAt;
    @Column(nullable = false)
    private boolean revoked = false;
    @Column(nullable = false)
    private String ipAddress;
    @Column(length = 255)
    private String userAgent;
}
