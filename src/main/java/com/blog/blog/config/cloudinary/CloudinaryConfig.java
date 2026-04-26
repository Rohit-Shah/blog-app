package com.blog.blog.config.cloudinary;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CloudinaryConfig {
    @Value("${CLOUDINARY_CLOUD_NAME}")
    private String cloudName;
    @Value("${CLOUDINARY_SECRET_KEY}")
    private String apiSecret;
    @Value("${CLOUDINARY_API_KEY}")
    private String apiKey;

    @Bean
    public Cloudinary cloudinary(){
        Cloudinary cloudinary = null;
        Map config = new HashMap<>();
        config.put("cloud_name",cloudName);
        config.put("api_secret",apiSecret);
        config.put("api_key",apiKey);
        cloudinary = new Cloudinary(config);
        return cloudinary;
    }

}
