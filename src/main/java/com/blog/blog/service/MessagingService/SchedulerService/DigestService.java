package com.blog.blog.service.MessagingService.SchedulerService;

import com.blog.blog.DTO.PostRequest.PostDTO;
import com.blog.blog.config.AppProperties;
import com.blog.blog.entity.PostEntity.Post;
import com.blog.blog.entity.UserEntity.User;
import com.blog.blog.repository.PostRepository.PostRepository;
import com.blog.blog.repository.SubscriptionRepository.SubscriptionRepository;
import com.blog.blog.repository.UserRepository.UserRepository;
import com.blog.blog.service.MessagingService.EmailService.EmailService;
import com.blog.blog.service.PostService.PostService;
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
import java.time.LocalDateTime;
import java.time.temporal.TemporalUnit;
import java.util.List;

@Service
@Slf4j
public class DigestService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SubscriptionRepository subscriptionRepository;
    @Autowired
    private PostService postService;
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
        log.debug("Started processing for user with user id : {} ",user.getUserId());
        Pageable topFollowings = PageRequest.of(0, appProperties.getTopFollowings());
        log.debug("Fetched top followings for the current user with user id : {} ",user.getUserId());
        //get the top followings
        List<Long> followingIds = subscriptionRepository.findTopFollowingsForUser(user.getUserId(),topFollowings);
        log.debug("Fetched following ids for the current user with user id : {} ",user.getUserId());
        if(CollectionUtils.isEmpty(followingIds)){
            log.debug("No followings for user {}", user.getUserId());
            return;
        }

        List<PostDTO> curatedList = postService.getRecentPostsForUsers(user.getUserId(),followingIds,cutoff);
        if(curatedList.isEmpty()) {
            return;
        }
        log.debug("Ready to send message to user with user id : {} ",user.getUserId());
        String mailBody = digestBuilder.buildWeeklyDigest(user,curatedList);
        log.debug("Mail body is ready : {} ", mailBody);
        emailService.sendHtmlEmail(user.getEmail(),"Weekly Recap",mailBody);
        log.info("Message delivered");
    }

}
