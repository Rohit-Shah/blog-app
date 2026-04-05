package com.blog.blog.service.serviceBean.OAuthProviderServiceBean;

import com.blog.blog.Response.AuthResponse;
import com.blog.blog.constants.AuthConstants.AuthConstants;
import com.blog.blog.constants.UserConstants.UserRoles;
import com.blog.blog.entity.AuthEntity.RefreshToken;
import com.blog.blog.entity.UserEntity.Role;
import com.blog.blog.entity.UserEntity.User;
import com.blog.blog.repository.AuthRepository.RefreshTokenRepository;
import com.blog.blog.repository.UserRepository.RoleRepository;
import com.blog.blog.repository.UserRepository.UserRepository;
import com.blog.blog.service.AuthService.JWTService;
import com.blog.blog.service.AuthService.OAuthProviderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@RequiredArgsConstructor
public class GoogleOAuthProvider implements OAuthProviderService {

    private final JWTService jwtService;
    private final RestTemplate restTemplate;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${spring.googleTokenExchangeUrl}")
    private String googleAuthTokenEndPoint;
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;
    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;
    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String redirectUri;

    @Override
    public AuthResponse authenticate(String authCode) {
        String tokenExchangeUrl = googleAuthTokenEndPoint;
        MultiValueMap<String,String> params = new LinkedMultiValueMap<>();
        params.add("code",authCode);
        params.add("client_id",clientId);
        params.add("client_secret",clientSecret);
        params.add("redirect_uri",redirectUri);
        params.add("grant_type","authorization_code");
        //set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String,String>> request = new HttpEntity<>(params,headers);

        //post request for access token
        ResponseEntity<Map> accessTokenResponse = restTemplate.postForEntity(tokenExchangeUrl, request, Map.class);
        String idToken = (String)accessTokenResponse.getBody().get("id_token");

        //get request for user details
        String userInfoUrl = "https://oauth2.googleapis.com/tokeninfo?id_token=" + idToken;
        ResponseEntity<Map> userInfoResponse = restTemplate.getForEntity(userInfoUrl, Map.class);
        if(userInfoResponse.getStatusCode() == HttpStatus.OK){
            Map userInfo = userInfoResponse.getBody();
            String email = (String) userInfo.get("email");
            String username = (String) userInfo.get("name");
            User user = null;
            try{
                user = userRepository.findUserByUsername(username);
                if(user == null){
                    throw new UsernameNotFoundException("No such user found");
                }
            } catch (Exception e){
                user = new User();
                user.setEmail(email);
                user.setUsername(username);
                user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
                List<Role> userRoles = new ArrayList<>();
                Role role = roleRepository.findByRoleName(UserRoles.USER.toString()).orElseThrow(() -> new RuntimeException("No such role found"));
                userRoles.add(role);
            }
            Date newAccessTokenExpirationTime = new Date(System.currentTimeMillis() + AuthConstants.ACCESS_TOKEN_EXPIRATION);
            Date newRefreshTokenExpirationTime = new Date(System.currentTimeMillis() + AuthConstants.REFRESH_TOKEN_EXPIRATION);
            String accessToken = jwtService.generateAccessToken(user,newAccessTokenExpirationTime);
            String refreshToken = jwtService.generateRefreshToken(user,newRefreshTokenExpirationTime);
            RefreshToken refreshTokenData = new RefreshToken();
            refreshTokenData.setToken(refreshToken);
            refreshTokenData.setRevoked(false);
            refreshTokenData.setUser(user);
            refreshTokenData.setExpiresAt(newRefreshTokenExpirationTime);
            refreshTokenRepository.save(refreshTokenData);
            return new AuthResponse(accessToken,refreshToken);
        }
        return new AuthResponse();
    }

}
