package com.blog.blog.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app.digest")
public class AppProperties {

    private String cron;
    private int userPageSize;
    private int topFollowings;
    private int maxCuratedPosts;
    private int postsPerFollowing;

}
