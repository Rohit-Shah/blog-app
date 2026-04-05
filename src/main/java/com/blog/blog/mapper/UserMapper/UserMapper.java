package com.blog.blog.mapper.UserMapper;

import com.blog.blog.DTO.UserRequest.UserDTO;
import com.blog.blog.DTO.UserRequest.UserRegistrationRequest;
import com.blog.blog.entity.UserEntity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    //Entity to DTO
    UserDTO toDTO(User user);

    User toEntity(UserRegistrationRequest userRegistrationRequest);


}
