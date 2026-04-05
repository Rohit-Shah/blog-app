package com.blog.blog.service.serviceBean.MessagingService.SchedulerService;

import com.blog.blog.DTO.PostRequest.PostDTO;
import com.blog.blog.config.AppProperties;
import com.blog.blog.entity.UserEntity.User;
import com.blog.blog.repository.SubscriptionRepository.SubscriptionRepository;
import com.blog.blog.repository.UserRepository.UserRepository;
import com.blog.blog.service.serviceBean.MessagingService.EmailService.EmailService;
import com.blog.blog.service.serviceBean.PostServiceBean.PostServiceBean;
import com.blog.blog.service.serviceBean.PostServiceBean.PostServiceBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
@Slf4j
public class DigestService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SubscriptionRepository subscriptionRepository;
    @Autowired
    private PostServiceBean postServiceBean;
    @Autowired
    private AppProperties appProperties;
    @Autowired
    private DigestBuilder digestBuilder;
    @Autowired
    private EmailService emailService;

    @Transactional(readOnly = true)
    public void processWeeklyDigest(){
        Instant cutoff = Instant.now().minus(Duration.ofDays(7));
        int userPageSize = appProperties.getUserPageSize();
        Pageable userPageable = PageRequest.of(0,userPageSize);
        Page<User> page;
        int pageNumber = 0;
        do{
            page = userRepository.findAll(PageRequest.of(pageNumber,userPageSize));
            log.debug("Fetched all users with size {} ", page.getSize());
            for(User user: page.getContent()){
                try{
                    processForUser(user,cutoff);
                }catch (Exception e){
                    log.error("Digest failed for user {}: {}", user.getUserId(), e.getMessage(), e);
                }
            }
            pageNumber++;
        }while(page.hasNext());
    }

    private void processForUser(User user, Instant cutoff){

    }

}
