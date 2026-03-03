package com.blog.blog.DTO.UserRequest;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class UserRegistrationRequest {
    private String username;
    private String email;
    private String password;
    private String firstName;
    private String middleName;
    private String lastName;
}
