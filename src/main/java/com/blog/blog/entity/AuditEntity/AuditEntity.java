package com.blog.blog.entity.AuditEntity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

@MappedSuperclass //(this doesn't create its own table - all these fields are mapped directly into its subclasses)
@EntityListeners(AuditingEntityListener.class)
@Getter
public abstract class AuditEntity {
    @CreatedDate
    @Column(nullable = false,updatable = false)
    private Date createdAt;
    @LastModifiedDate
    @Column(nullable = false)
    private Date updatedAt;
}
