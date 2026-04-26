package com.blog.blog.service.AuthService;

import com.blog.blog.entity.AuthEntity.RefreshToken;
import com.blog.blog.entity.UserEntity.User;

import java.util.Date;
import java.util.Optional;

public interface RefreshTokenService {
    void saveTokenData(User user, String token, Date refreshTokenExpiryTime,String ipAddress,String userAgent);

    void revokeAllTokens(Long userId);

    Optional<RefreshToken> getTokenDetails(String refreshToken);

    void revokeToken(RefreshToken token);
}
