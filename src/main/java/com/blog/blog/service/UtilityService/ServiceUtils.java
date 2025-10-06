package com.blog.blog.service.UtilityService;

import com.blog.blog.entity.UserEntity.User;
import com.blog.blog.repository.UserRepository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ServiceUtils {

    @Autowired
    private UserRepository userRepository;

    public User getLoggedInUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findUserByUsername(username);
        if(user == null){
            throw new UsernameNotFoundException("No such user found");
        }
        return user;
    }

}
