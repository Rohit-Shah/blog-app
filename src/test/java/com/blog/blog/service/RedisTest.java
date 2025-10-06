package com.blog.blog.service;

import com.blog.blog.service.RedisService.RedisService;
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

    @Test
    @Disabled
    void testRedisConfig(){
        redisTemplate.opsForValue().set("email","mail@gmail.com");
    }

    @Test
    @Disabled
    void testRedisService(){
        redisService.set("posts","These are my posts",300l);
    }
}
