package com.blog.blog.service.UserService;

import com.blog.blog.DTO.UserRequest.UserDTO;
import com.blog.blog.constants.UserConstants.UserConstants;
import com.blog.blog.entity.UserEntity.User;
import com.blog.blog.entity.UserEntity.UserPrincipal;
import com.blog.blog.repository.UserRepository.UserRepository;
import com.blog.blog.service.PostService.PostService;
import com.blog.blog.service.RedisService.RedisService;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RedisService redisService;

    @Autowired
    private PostService postService;

    public UserDTO getUserProfileDetails(String userIdStr) {
        Long userId = Long.parseLong(userIdStr);
        try{
            String userKey = "User_" + userIdStr;
            TypeReference<User> userTypeReference = new TypeReference<User>() {};
            if(redisService.get(userKey,userTypeReference) != null){
                return convertUserEntityToDTO(redisService.get(userKey,userTypeReference));
            }
            else{
                User dbUser = userRepository.findUserByUserId(userId);
                redisService.set(userKey,dbUser,UserConstants.USER_PROFILE_EXPIRATION_TIME);
                if(dbUser != null){
                    return convertUserEntityToDTO(dbUser);
                }
            }
        }catch (UsernameNotFoundException e){
            log.error("Error => {}", e.getMessage());
            throw e;
        }
        return null;
    }

    private UserDTO convertUserEntityToDTO(User user){
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        userDTO.setTotalFollowers(user.getFollowerCount());
        userDTO.setTotalFollowing(user.getFollowingCount());
        return userDTO;
    }

    private User convertUserDTOToEntity(UserDTO userDTO){
        User user = new User();
        return user;
    }
}
