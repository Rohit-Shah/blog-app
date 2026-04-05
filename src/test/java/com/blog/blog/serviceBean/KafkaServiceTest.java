package com.blog.blog.serviceBean;

import com.blog.blog.service.serviceBean.MessagingService.KafkaEventPublisher;
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
