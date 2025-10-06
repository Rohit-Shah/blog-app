package com.blog.blog.service.MessagingService;

import com.blog.blog.entity.UserEntity.User;
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
            log.error("Exception while sending email {} ", e.getMessage());
        }
    }

    public void sendUserSubscriptionMail(SimpleMailMessage mailMessage) {
        try{
            javaMailSender.send(mailMessage);
        }catch (Exception e){
            log.error("Exception while sending email {}",e.getMessage());
        }
    }
}
