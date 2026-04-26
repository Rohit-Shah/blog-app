package com.blog.blog.mapper.AuthMapper;

import com.blog.blog.DTO.UserRequest.UserDTO;
import com.blog.blog.Response.AuthResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthMapper {
    AuthResponse toAuthResponse(UserDTO userDTO, String accessToken, String refreshToken);
}
