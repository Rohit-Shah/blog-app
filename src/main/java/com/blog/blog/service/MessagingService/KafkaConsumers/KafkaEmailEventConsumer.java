package com.blog.blog.service.MessagingService.KafkaConsumers;

import com.blog.blog.DTO.MessagingEntity.EmailEvent;
import com.blog.blog.service.MessagingService.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaEmailEventConsumer {

    @Autowired
    private EmailService emailService;

    @KafkaListener(topics = "email.notifications.user.subscriptions", groupId = "email-notifications-group")
    public void consumeMessage(EmailEvent emailEvent){
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(emailEvent.getSendToEmail());
        mailMessage.setSubject(emailEvent.getMailSubject());
        mailMessage.setText(emailEvent.getMailContent());
        try{
            emailService.sendUserSubscriptionMail(mailMessage);
        }catch (Exception e){
            log.error("Error : {} ", e.getMessage());
        }
    }

}
