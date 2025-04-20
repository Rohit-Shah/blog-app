package com.blog.blog.service;

import com.blog.blog.service.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EmailServiceTest {
    @Autowired
    private EmailService emailService;

    @Test
    void testSendMail(){
        emailService.sendEmail("email@gmail.com","Testing Java mail sender","Hi , Successfully sent my first email using java mail sender");
    }
}
