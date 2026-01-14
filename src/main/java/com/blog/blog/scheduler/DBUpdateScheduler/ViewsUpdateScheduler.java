package com.blog.blog.scheduler.DBUpdateScheduler;

import com.blog.blog.service.PostService.PostService;
import com.blog.blog.service.RedisService.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Component
public class ViewsUpdateScheduler {

    @Autowired
    private RedisService redisService;
    @Autowired
    private PostService postService;

    @Scheduled(cron = "0 */5 * * * *")
    @Transactional
    public void scheduleTask(){
        //to be updated
        String dirtyKey = "post:view:dirty";
        Set<String> dirtyPostList = redisService.getDirtyPosts(dirtyKey);
        if(dirtyPostList == null || dirtyPostList.isEmpty()) return;
        for(String postId: dirtyPostList){
            String postViewKey = "post:view:" + Long.valueOf(postId);
            Long postViews = redisService.getPostViewDetails(postViewKey);
            postService.updatePostViewDetailsInDB(Long.valueOf(postId),postViews);
            redisService.clearDirtyPosts(dirtyKey,postId);
        }
    }

}
