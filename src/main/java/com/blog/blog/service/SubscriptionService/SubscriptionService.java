package com.blog.blog.service.SubscriptionService;

import com.blog.blog.DTO.MessagingEntity.EmailEvent;
import com.blog.blog.constants.MessagingConstants.KafkaEvents;
import com.blog.blog.service.MessagingService.EmailService;
import com.blog.blog.service.MessagingService.KafkaEventPublisher;
import com.blog.blog.entity.SubscriptionEntity.Subscription;
import com.blog.blog.entity.UserEntity.User;
import com.blog.blog.entity.UserEntity.UserPrincipal;
import com.blog.blog.repository.SubscriptionRepository.SubscriptionRepository;
import com.blog.blog.repository.UserRepository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SubscriptionService {

    @Autowired
    private SubscriptionRepository subscriptionRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private KafkaEventPublisher kafkaEventPublisher;

    public String subscribe(UserPrincipal userPrincipal, Long userId) {
        User subscriber = userPrincipal.getUser();
        if(subscriber == null){
            throw new UsernameNotFoundException("Please log in to subscribe");
        }
        User toBeSubscribedUser = userRepository.findUserByUserId(userId);
        if(toBeSubscribedUser == null){
            throw new UsernameNotFoundException("The user that you are trying to subscribe no longer exists");
        }
        Subscription doesSubscriptionExists = subscriptionRepository.findSubscriptionBySubscriberAndSubscribedTo(subscriber,toBeSubscribedUser);
        if(doesSubscriptionExists != null){
            return unSubscribe(userPrincipal,userId);
        }
        Subscription subscription = new Subscription();
        subscription.setSubscriber(subscriber);
        subscription.setSubscribedTo(toBeSubscribedUser);
        subscriptionRepository.save(subscription);
        EmailEvent emailEventPayload = new EmailEvent("USER_SUBSCRIPTION","New Subscriber","Hurray!! You got a new Subscriber." + subscriber.getUsername() + " subscribed you.",toBeSubscribedUser.getEmail());
        kafkaEventPublisher.sendMessage(KafkaEvents.EMAIL_NOTIFICATIONS.getTopic(),emailEventPayload);
        return "Subscription added successfully";
    }

    @Transactional
    public String unSubscribe(UserPrincipal userPrincipal, Long userId) {
        User subscriber = userPrincipal.getUser();
        if(subscriber == null){
            throw new UsernameNotFoundException("Please log in to subscribe");
        }
        User toBeUnSubscribedUser = userRepository.findUserByUserId(userId);
        if(toBeUnSubscribedUser == null){
            throw new UsernameNotFoundException("The user that you are trying to subscribe no longer exists");
        }
        subscriptionRepository.deleteSubscription(subscriber.getUserId(),toBeUnSubscribedUser.getUserId());
        return "Subscription removed successfully";
    }
}
