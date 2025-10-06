package com.blog.blog.DTO.MessagingEntity;

import com.blog.blog.entity.UserEntity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailEvent {
    private String eventType;
    private String mailSubject;
    private String mailContent;
    private String sendToEmail;
}
