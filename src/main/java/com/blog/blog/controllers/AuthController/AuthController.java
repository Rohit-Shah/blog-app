package com.blog.blog.controllers.AuthController;

import com.blog.blog.DTO.UserRequest.UserDTO;
import com.blog.blog.DTO.UserRequest.UserLoginRequest;
import com.blog.blog.DTO.UserRequest.UserRegistrationRequest;
import com.blog.blog.Exceptions.UserAlreadyExistsException;
import com.blog.blog.Exceptions.UserLockedException;
import com.blog.blog.Response.ApiResponse;
import com.blog.blog.Response.AuthResponse;
import com.blog.blog.service.AuthService.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    //register user
    @PostMapping("/register-user")
    public ResponseEntity<ApiResponse> registerUser(@RequestBody UserRegistrationRequest userData){
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
    public ResponseEntity<ApiResponse> loginUser(@RequestBody UserLoginRequest userData) throws AuthenticationException {
        AuthResponse loggedInUser = authService.loginUser(userData);
        ApiResponse successResponse = new ApiResponse("User login successful",true,loggedInUser);
        return ResponseEntity.status(HttpStatus.OK).body(successResponse);
    }

    @PostMapping("/refresh-access-token")
    public ResponseEntity<ApiResponse> refreshAccessToken(@RequestBody Map<String,String> request){
        try{
            String refreshToken = request.get("refreshToken");
            String newAccessToken = authService.generateNewAccessToken(refreshToken);
            ApiResponse successResponse = new ApiResponse("Access token refreshed",true,newAccessToken);
            return ResponseEntity.status(HttpStatus.OK).body(successResponse);
        }catch (Exception e){
            ApiResponse errorResponse = new ApiResponse(e.getMessage(),false,null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

}