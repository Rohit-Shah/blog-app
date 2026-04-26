package com.blog.blog.service.serviceBean.AuthService;

import com.blog.blog.entity.AuthEntity.RefreshToken;
import com.blog.blog.entity.UserEntity.User;
import com.blog.blog.mapper.AuthMapper.RefreshTokenMapper;
import com.blog.blog.repository.AuthRepository.RefreshTokenRepository;
import com.blog.blog.service.AuthService.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceBean implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenMapper refreshTokenMapper;

    @Override
    public void saveTokenData(User user, String token, Date expiresAt, String ipAddress, String userAgent) {
        RefreshToken refreshToken = refreshTokenMapper.toRefreshToken(user, token, expiresAt, ipAddress, userAgent);
        refreshTokenRepository.save(refreshToken);
    }

    @Override
    public void revokeAllTokens(Long userId) {
        refreshTokenRepository.revokeAllTokens(userId);
    }

    @Override
    public Optional<RefreshToken> getTokenDetails(String refreshToken) {
        return refreshTokenRepository.findByToken(refreshToken);
    }

    @Override
    public void revokeToken(RefreshToken token) {
        token.setRevoked(true);
        refreshTokenRepository.save(token);
    }
}
