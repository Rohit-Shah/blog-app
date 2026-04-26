package com.blog.blog.mapper.AuthMapper;

import com.blog.blog.entity.AuthEntity.RefreshToken;
import com.blog.blog.entity.UserEntity.User;
import org.mapstruct.Mapper;

import java.util.Date;

@Mapper(componentModel = "spring")
public interface RefreshTokenMapper {

    RefreshToken toRefreshToken(User user, String token, Date expiresAt, String ipAddress, String userAgent);

}
