package com.blog.blog.controllers.AuthController;

import com.blog.blog.DTO.AuthRequest.RefreshTokenRequest;
import com.blog.blog.DTO.UserRequest.UserDTO;
import com.blog.blog.DTO.UserRequest.UserLoginRequest;
import com.blog.blog.DTO.UserRequest.UserRegistrationRequest;
import com.blog.blog.Response.ApiResponse;
import com.blog.blog.Response.AuthResponse;
import com.blog.blog.service.AuthService.AuthService;
import com.blog.blog.service.AuthService.RegistrationService;
import com.blog.blog.service.TokenService.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final RegistrationService registrationService;
    private final AuthService authService;
    private final TokenService tokenService;

    //register user
    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registerUser(@RequestBody UserRegistrationRequest userData){
        UserDTO registeredUser = registrationService.register(userData);
        ApiResponse successResponse = new ApiResponse("User Registered Successfully",true,registeredUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(successResponse);

    }

    //login user
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> loginUser(@RequestBody UserLoginRequest userData, HttpServletRequest httpServletRequest) throws AuthenticationException {
        AuthResponse loggedInUser = authService.login(userData,httpServletRequest);
        ApiResponse successResponse = new ApiResponse("User login successful",true,loggedInUser);
        return ResponseEntity.status(HttpStatus.OK).body(successResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse> refreshAccessToken(@RequestBody RefreshTokenRequest refreshTokenRequest){
        String refreshToken = refreshTokenRequest.getRefreshToken();
        AuthResponse newAccessToken = tokenService.rotateRefreshToken(refreshToken);
        ApiResponse successResponse = new ApiResponse("Token refreshed",true,newAccessToken);
        return ResponseEntity.status(HttpStatus.OK).body(successResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logoutUser(@RequestBody RefreshTokenRequest refreshTokenRequest){
        String refreshToken = refreshTokenRequest.getRefreshToken();
        AuthResponse logoutResponse = authService.logout(refreshToken);
        ApiResponse successResponse = new ApiResponse("logout success",true,null);
        return ResponseEntity.status(HttpStatus.OK).body(successResponse);
    }

}