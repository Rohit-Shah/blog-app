package com.blog.blog.service;

import com.blog.blog.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailService {
    @Autowired
    private JavaMailSender javaMailSender;

    public void sendEmail(String to,String subject,String body){
        try{
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setTo(to);
            simpleMailMessage.setSubject(subject);
            simpleMailMessage.setText(body);
            javaMailSender.send(simpleMailMessage);
        }catch (Exception e){
            log.error("Exception while sending email" + e.getMessage());
        }
    }

    public void sendMailNotificationToSubscribedUser(User subscriber, User toBeSubscribedUser) {
        try{
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            String subscribedUserEmail = toBeSubscribedUser.getEmail();
            String subscribedUserUsername = toBeSubscribedUser.getUsername();
            String subscriberUserName = subscriber.getUsername();
            mailMessage.setTo(subscribedUserEmail);
            mailMessage.setSubject("Congrats !!! You got a new subscriber");
            mailMessage.setText("Hi " + subscribedUserUsername + ", \n"
             + subscriberUserName + " just subscribed you.");
            javaMailSender.send(mailMessage);
        }catch (Exception e){
            log.error("Exception while sending email" + e.getMessage());
        }
    }
}
