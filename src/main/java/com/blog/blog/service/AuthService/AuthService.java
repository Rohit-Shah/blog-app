package com.blog.blog.service.AuthService;

import com.blog.blog.DTO.UserRequest.UserDTO;
import com.blog.blog.DTO.UserRequest.UserLoginRequest;
import com.blog.blog.DTO.UserRequest.UserRegistrationRequest;
import com.blog.blog.Response.AuthResponse;
import jakarta.servlet.http.HttpServletRequest;

import javax.naming.AuthenticationException;

public interface AuthService {

    AuthResponse login(UserLoginRequest userData, HttpServletRequest httpServletRequest) throws AuthenticationException;
    AuthResponse logout(String refreshToken);
}
