package com.blog.blog.constants.MessagingConstants;


public enum KafkaEvents {

    EMAIL_NOTIFICATIONS("email.notifications.user.subscriptions"),
    USER_ACTIVITY("user.activity");

    private String topic;

    KafkaEvents(String topic){
        this.topic = topic;
    }

    public String getTopic(){
        return this.topic;
    }

}
