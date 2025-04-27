package com.blog.blog.controllers;

import com.blog.blog.DTO.UserDTO;
import com.blog.blog.Exceptions.JWTValidationException;
import com.blog.blog.Response.ApiResponse;
import com.blog.blog.entity.User;
import com.blog.blog.repository.UserRepository;
import com.blog.blog.service.CustomUserDetailsService;
import com.blog.blog.service.JWTService;
import com.blog.blog.service.ServiceUtils;
import com.blog.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @GetMapping("/get-user-profile-details/{userId}")
    private ResponseEntity<ApiResponse> getUserDetails(@PathVariable String userId){
        try{
            UserDTO userDTO = userService.getUserProfileDetails(userId);
            ApiResponse successResponse = new ApiResponse("User Details",true,userDTO);
            return ResponseEntity.status(HttpStatus.OK).body(successResponse);
        }catch (UsernameNotFoundException e){
            ApiResponse errorResponse = new ApiResponse(e.getMessage(),false,null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }catch (Exception e){
            ApiResponse errorResponse = new ApiResponse(e.getMessage(),false,null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
