package com.blog.blog.service.AuthService;


import com.blog.blog.DTO.UserRequest.UserDTO;
import com.blog.blog.DTO.UserRequest.UserRegistrationRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

public interface RegistrationService {

    UserDTO register(@RequestBody UserRegistrationRequest userRegistrationRequest);

}
