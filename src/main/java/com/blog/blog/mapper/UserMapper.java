package com.blog.blog.mapper;

import com.blog.blog.DTO.UserRequest.UserDTO;
import com.blog.blog.DTO.UserRequest.UserRegistrationRequest;
import com.blog.blog.entity.UserEntity.Role;
import com.blog.blog.entity.UserEntity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface UserMapper {

    //Entity to DTO
    UserDTO toDTO(User user);

    User toEntity(UserRegistrationRequest userRegistrationRequest);


}
