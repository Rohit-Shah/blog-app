package com.blog.blog.repository.SubscriptionRepository;

import com.blog.blog.entity.SubscriptionEntity.Subscription;
import com.blog.blog.entity.UserEntity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface SubscriptionRepository extends JpaRepository<Subscription,Long> {
    Subscription findSubscriptionBySubscriberAndSubscribedTo(User subscriber,User subscribedTo);
    @Transactional
    @Modifying
    @Query("DELETE FROM Subscription s WHERE s.subscriber.userId = :subscriberId and s.subscribedTo.userId = :subscribedToId ")
    void deleteSubscription(Long subscriberId,Long subscribedToId);

    @Query("""
            SELECT s.subscribedTo.userId FROM Subscription s
            WHERE s.subscriber.userId = :userId
            GROUP BY s.subscribedTo.userId
            ORDER BY COUNT(s.subscribedTo.userId) DESC
            """)
    List<Long> findTopFollowingsForUser(Long userId, Pageable pageable);
}
