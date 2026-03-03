package com.blog.blog.serviceBean.AuthService;

import com.blog.blog.DTO.UserRequest.UserDTO;
import com.blog.blog.DTO.UserRequest.UserLoginRequest;
import com.blog.blog.Response.AuthResponse;
import com.blog.blog.constants.AuthConstants.AuthConstants;
import com.blog.blog.constants.UserConstants.UserProfileStatus;
import com.blog.blog.entity.AuthEntity.RefreshToken;
import com.blog.blog.entity.UserEntity.User;
import com.blog.blog.entity.UserEntity.UserPrincipal;
import com.blog.blog.mapper.UserMapper;
import com.blog.blog.repository.AuthRepository.RefreshTokenRepository;
import com.blog.blog.repository.UserRepository.RoleRepository;
import com.blog.blog.service.AuthService.AuthService;
import com.blog.blog.service.AuthService.JWTService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.AuthenticationException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthServiceBean implements AuthService {
    private User loggedInUser;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final RoleRepository roleRepository;
    private final CustomUserDetailsService userDetailsService;
    private final LoginAttemptService loginAttemptService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserMapper mapper;


    @Transactional
    @Override
    public AuthResponse login(UserLoginRequest userData, HttpServletRequest httpServletRequest) throws AuthenticationException {
        String accessToken = "";
        String refreshToken = "";
        String ipAddress = getClientIPAddress(httpServletRequest);
        String userAgent = httpServletRequest.getHeader("User-Agent");
        String username = userData.getUsername();
        String password = userData.getPassword();
        try{
            Authentication authUser = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username,password));
            UserPrincipal userPrincipal = (UserPrincipal) authUser.getPrincipal();
            User user = userPrincipal.getUser();
            if(user.getStatus() != UserProfileStatus.ACTIVE) {
                throw new DisabledException("User profile is not active");
            }
            Date refreshTokenExpiryTime = new Date(System.currentTimeMillis() + AuthConstants.ACCESS_TOKEN_EXPIRATION);
            Date accessTokenExpiryTime = new Date(System.currentTimeMillis() + AuthConstants.REFRESH_TOKEN_EXPIRATION);
            if(authUser.isAuthenticated()){
                accessToken = jwtService.generateAccessToken(user,accessTokenExpiryTime);
                refreshToken = jwtService.generateRefreshToken(user,refreshTokenExpiryTime);
                AuthResponse loggedInUser = new AuthResponse();
                loggedInUser.setAccessToken(accessToken);
                loggedInUser.setRefreshToken(refreshToken);
                UserDTO userDTO = mapper.toDTO(user);
                loggedInUser.setUser(userDTO);
                refreshTokenRepository.revokeAllTokens(user.getUserId());
                RefreshToken refreshTokenData = new RefreshToken();
                refreshTokenData.setUser(user);
                refreshTokenData.setToken(refreshToken);
                refreshTokenData.setExpiresAt(refreshTokenExpiryTime);
                refreshTokenData.setIpAddress(ipAddress);
                refreshTokenData.setUserAgent(userAgent);
                refreshTokenRepository.save(refreshTokenData);
                loginAttemptService.loginSucceed(user.getUsername());
                return loggedInUser;
            }
        }catch (LockedException e){
            long lastAttemptTime = loginAttemptService.getUserLastLoginDetails(username);
            long timeRemaining = 15;
            if(lastAttemptTime != -1){
                timeRemaining = 15 - TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - lastAttemptTime);
            }
            throw new LockedException("Too many unsuccessful attempts !! Please try again after " + timeRemaining + " mins");
        }
        catch (BadCredentialsException e){
            loginAttemptService.loginFailed(userData.getUsername());
            throw new AuthenticationException("Invalid credentials");
        }
        throw new UsernameNotFoundException("No user found for the given username");
    }

    private String getClientIPAddress(HttpServletRequest httpServletRequest){
        String xForwardedFor = httpServletRequest.getHeader("X-Forwarded-For");
        if(xForwardedFor != null && !xForwardedFor.isEmpty()){
            return xForwardedFor.split(",")[0];
        }
        return httpServletRequest.getRemoteAddr();
    }

    public AuthResponse logout(String refreshToken) {
        Optional<RefreshToken> currToken = refreshTokenRepository.findByToken(refreshToken);
        if(currToken.isEmpty()) return new AuthResponse();
        RefreshToken token = currToken.get();
        token.setRevoked(true);
        refreshTokenRepository.save(token);
        return new AuthResponse();
    }

}
