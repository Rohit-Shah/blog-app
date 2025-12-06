package com.blog.blog.scheduler.BlogDigest;

import com.blog.blog.service.MessagingService.SchedulerService.DigestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@Slf4j
public class DigestScheduler {
    @Autowired
    private DigestService digestService;


    @Scheduled(cron = "${app.digest.cron}")
    public void runWeeklyDigest(){
        log.debug("Started weekly digest for date {} ", Instant.now());
        digestService.processWeeklyDigest();
    }

}
