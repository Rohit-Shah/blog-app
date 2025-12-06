package com.blog.blog.service.MessagingService.EmailService;

import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Slf4j
@Service
public class EmailService {
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private Executor emailTaskExecutor;

    public void sendSimpleEmail(String to,String subject,String body){
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

    @Async("emailTaskExecutor")
    public void sendHtmlEmail(String to, String subject, String body){
        CompletableFuture.runAsync(() -> {
            try {
                MimeMessage mimeMessage = javaMailSender.createMimeMessage();
                MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
                mimeMessageHelper.setTo(to);
                mimeMessageHelper.setSubject(subject);
                mimeMessageHelper.setText(body, true);
                javaMailSender.send(mimeMessage);
            } catch (Exception e) {
                log.error("Failed to send email to {} - {}", to, e.getMessage(), e);
            }
        }, emailTaskExecutor);
    }

    public void sendUserSubscriptionMail(SimpleMailMessage mailMessage) {
        try{
            javaMailSender.send(mailMessage);
        }catch (Exception e){
            log.error("Exception while sending email {}",e.getMessage());
        }
    }
}
