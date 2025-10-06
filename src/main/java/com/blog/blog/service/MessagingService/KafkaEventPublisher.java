package com.blog.blog.service.MessagingService;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaEventPublisher {

    @Autowired
    private KafkaTemplate<String,Object> kafkaTemplate;

    public void sendMessage(String topicName,Object payload){

        try{
            kafkaTemplate.send(topicName,payload);
        }catch (Exception e){
            log.error("Error while sending message {}", e.getMessage());
        }
    }

    public void sendMessage(String topicName,String partitionKey,Object payload){
        try{
            kafkaTemplate.send(topicName,partitionKey,payload);
        }catch (Exception e){
            log.error("Error while sending message {}", e.getMessage());
        }
    }

}
