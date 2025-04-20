package com.blog.blog.controllers;

import com.blog.blog.DTO.UserDTO;
import com.blog.blog.Exceptions.UserAlreadyExistsException;
import com.blog.blog.Response.ApiResponse;
import com.blog.blog.entity.User;
import com.blog.blog.entity.UserPrincipal;
import com.blog.blog.repository.UserRepository;
import com.blog.blog.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    //register user
    @PostMapping("/register-user")
    public ResponseEntity<ApiResponse> registerUser(@RequestBody UserDTO userData){
        try{
            UserDTO registeredUser = authService.registerUser(userData);
            ApiResponse successResponse = new ApiResponse("User Registered Successfully",false,registeredUser);
            return ResponseEntity.status(HttpStatus.OK).body(successResponse);
        }catch (UserAlreadyExistsException e){
            ApiResponse errorResponse = new ApiResponse(e.getMessage(),false,null);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }catch (Exception e){
            ApiResponse errorResponse = new ApiResponse("Internal Server Error occurred. Please try again",false,null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    //login user
    @PostMapping("/login-user")
    public ResponseEntity<ApiResponse> loginUser(@RequestBody UserDTO userData){
        try{
            UserDTO loggedInUser = authService.loginUser(userData);
            ApiResponse successResponse = new ApiResponse("User login successful",true,loggedInUser);
            return ResponseEntity.status(HttpStatus.OK).body(successResponse);
        }catch (UsernameNotFoundException e){
            ApiResponse errorResponse = new ApiResponse("User not found",false,null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }catch (Exception e){
            ApiResponse errorResponse = new ApiResponse("Some error Occurred",false,null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/refresh-access-token")
    public ResponseEntity<ApiResponse> refreshAccessToken(@RequestBody String refreshToken){
        try{
            String newAccessToken = authService.generateNewAccessToken(refreshToken);
            ApiResponse successResponse = new ApiResponse("Access token refreshed",true,newAccessToken);
            return ResponseEntity.status(HttpStatus.OK).body(successResponse);
        }catch (Exception e){
            ApiResponse errorResponse = new ApiResponse(e.getMessage(),false,null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

}
