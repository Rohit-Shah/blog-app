package com.blog.blog.service;

import com.blog.blog.service.MessagingService.EmailService.EmailService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EmailServiceTest {
    @Autowired
    private EmailService emailService;

    @Test
    @Disabled
    void testSendMail(){
        emailService.sendSimpleEmail("email@gmail.com","Testing Java mail sender","Hi , Successfully sent my first email using java mail sender");
    }
}
