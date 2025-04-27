package com.blog.blog.service;

import com.blog.blog.DTO.UserDTO;
import com.blog.blog.Exceptions.JWTValidationException;
import com.blog.blog.entity.User;
import com.blog.blog.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;
    public UserDTO getUserProfileDetails(String userIdStr) {
        Long userId = Long.parseLong(userIdStr);
        try{
            User dbUser = userRepository.findUserByUserId(userId);
            if(dbUser != null){
                return convertUserEntityToDTO(dbUser);
            }
        }catch (UsernameNotFoundException e){
            log.error("Error => " + e.getMessage());
            throw e;
        }
        return null;
    }

    private UserDTO convertUserEntityToDTO(User user){
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        return userDTO;
    }

    private User convertUserDTOToEntity(UserDTO userDTO){
        User user = new User();
        return user;
    }
}
