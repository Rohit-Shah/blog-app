package com.blog.blog.repository;

import com.blog.blog.entity.Subscription;
import com.blog.blog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface SubscriptionRepository extends JpaRepository<Subscription,Long> {
    Subscription findSubscriptionBySubscriberAndSubscribedTo(User subscriber,User subscribedTo);
    @Transactional
    @Modifying
    @Query("DELETE FROM Subscription s WHERE s.subscriber.userId = :subscriberId and s.subscribedTo.userId = :subscribedToId ")
    void deleteSubscription(Long subscriberId,Long subscribedToId);
}
