package com.blog.blog.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "subscriptions")
@Data
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long subscriptionId;
    @ManyToOne
    @JoinColumn(name = "subscriber_id",nullable = false)
    private User subscriber;
    @ManyToOne
    @JoinColumn(name = "subscribed_to_id",nullable = false)
    private User subscribedTo;
    @CreationTimestamp
    private Instant subscribedAt;
}
