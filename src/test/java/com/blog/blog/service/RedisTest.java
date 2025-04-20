package com.blog.blog.service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
public class RedisTest {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedisService redisService;

    @Disabled
    @Test
    void testRedisConfig(){
        redisTemplate.opsForValue().set("email","mail@gmail.com");
    }

    @Test
    void testRedisService(){
        redisService.set("posts","These are my posts",300l);
    }
}
