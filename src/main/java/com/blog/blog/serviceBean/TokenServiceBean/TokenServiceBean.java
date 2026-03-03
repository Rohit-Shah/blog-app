package com.blog.blog.serviceBean.TokenServiceBean;

import com.blog.blog.Exceptions.JWTValidationException;
import com.blog.blog.Response.AuthResponse;
import com.blog.blog.constants.AuthConstants.AuthConstants;
import com.blog.blog.entity.AuthEntity.RefreshToken;
import com.blog.blog.entity.UserEntity.User;
import com.blog.blog.entity.UserEntity.UserPrincipal;
import com.blog.blog.repository.AuthRepository.RefreshTokenRepository;
import com.blog.blog.service.AuthService.JWTService;
import com.blog.blog.service.TokenService.TokenService;
import com.blog.blog.serviceBean.AuthService.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class TokenServiceBean implements TokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final CustomUserDetailsService userDetailsService;
    private final JWTService jwtService;

    @Transactional
    @Override
    public AuthResponse rotateRefreshToken(String refreshToken) {

        if(!jwtService.validateToken(refreshToken,"REFRESH")){
            throw new JWTValidationException("Invalid or Expired refresh token");
        }
        //validate DB token
        RefreshToken dbToken = refreshTokenRepository.findByToken(refreshToken).orElseThrow(() -> new JWTValidationException("Invalid or Expired refresh token"));

        //check validity of token
        if(dbToken.isRevoked() || dbToken.getExpiresAt().before(new Date())){
            throw new JWTValidationException("Invalid or Expired refresh token");
        }

        String username = jwtService.extractUsername(refreshToken);
        UserPrincipal userPrincipal = (UserPrincipal) userDetailsService.loadUserByUsername(username);
        if(userPrincipal == null){
            throw new UsernameNotFoundException("No such user found for the given username");
        }
        dbToken.setRevoked(true);
        refreshTokenRepository.save(dbToken);
        User user = userPrincipal.getUser();
        Date newAccessTokenExpirationTime = new Date(System.currentTimeMillis() + AuthConstants.ACCESS_TOKEN_EXPIRATION);
        Date newRefreshTokenExpirationTime = new Date(System.currentTimeMillis() + AuthConstants.REFRESH_TOKEN_EXPIRATION);
        String newRefreshToken = jwtService.generateRefreshToken(user,newRefreshTokenExpirationTime);
        String newAccessToken = jwtService.generateAccessToken(user,newAccessTokenExpirationTime);
        RefreshToken rotatedRefreshToken = new RefreshToken();
        rotatedRefreshToken.setRevoked(false);
        rotatedRefreshToken.setExpiresAt(newRefreshTokenExpirationTime);
        rotatedRefreshToken.setUser(user);
        rotatedRefreshToken.setToken(newRefreshToken);
        refreshTokenRepository.save(rotatedRefreshToken);
        return new AuthResponse(newAccessToken,newRefreshToken);
    }

    @Override
    public void revokeToken(String refreshToken) {

    }

}
