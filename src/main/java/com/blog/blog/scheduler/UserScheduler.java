package com.blog.blog.scheduler;

import com.blog.blog.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class UserScheduler {

    @Autowired
    private EmailService emailService;

//    @Scheduled(cron = "0 * * ? * *")
    public void sendBlogUpdatesToSubscribers(){
        emailService.sendEmail("email@gmail.com","Scheduled message","This message was scheduled to be delivered at 11:45");
    }
}
