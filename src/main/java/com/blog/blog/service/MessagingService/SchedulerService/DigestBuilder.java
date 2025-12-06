package com.blog.blog.service.MessagingService.SchedulerService;

import com.blog.blog.DTO.PostRequest.PostDTO;
import com.blog.blog.entity.UserEntity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DigestBuilder {

    @Autowired
    private TemplateEngine templateEngine;

    public String buildWeeklyDigest(User user, List<PostDTO> posts){
        Context context = new Context();
        context.setVariable("user",user);
        context.setVariable("posts",posts);
        context.setVariable("generatedAt", LocalDateTime.now());
        context.setVariable("baseUrl","http://localhost/5173");
        return templateEngine.process("email/post-summary",context);
    }

}
