package com.blog.blog.service;

import com.blog.blog.service.MessagingService.KafkaEventPublisher;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class KafkaServiceTest {

    @Autowired
    private KafkaEventPublisher kafkaEventPublisher;

//    @Test
//    @Disabled
//    public void sendMessageToKafkaPublisher(){
//        kafkaEventPublisher.sendMessage();
//    }

}
