package com.blog.blog.service;

import com.blog.blog.entity.Subscription;
import com.blog.blog.entity.User;
import com.blog.blog.entity.UserPrincipal;
import com.blog.blog.repository.SubscriptionRepository;
import com.blog.blog.repository.UserRepository;
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
        emailService.sendMailNotificationToSubscribedUser(subscriber,toBeSubscribedUser);
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
