package com.blog.blog.entity.UserEntity;

import com.blog.blog.constants.UserConstants.UserProfileStatus;
import com.blog.blog.entity.AuditEntity.AuditEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "users",
    indexes = {
//        @Index(name = "idx_user_email",columnList = "email"),
        @Index(name = "idx_user_username", columnList = "username")
    }
)
public class User extends AuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    @Column(unique = true, nullable = false,length = 10)
    private String username;
    @Column(nullable = false,length = 50)
    private String firstName;
    @Column(length = 50)
    private String middleName;
    @Column(nullable = false,length = 50)
    private String lastName;
    @Column(unique = true,nullable = false,length = 255)
    private String email;
    @Column(nullable = false)
    private String password;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name="user_id"),
            inverseJoinColumns = @JoinColumn(name="role_id")
    )
    private Set<Role> roles = new HashSet<>();
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserProfileStatus status;
    @Column(nullable = false)
    private boolean deleted = false;
    @Column(nullable = false)
    private int tokenVersion = 0;
}
