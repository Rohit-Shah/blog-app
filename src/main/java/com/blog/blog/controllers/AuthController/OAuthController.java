package com.blog.blog.controllers.AuthController;

import com.blog.blog.Response.ApiResponse;
import com.blog.blog.Response.AuthResponse;
import com.blog.blog.service.AuthService.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
public class OAuthController {

    @Autowired
    private AuthService authService;
    @Value("${spring.frontEndBaseUrl}")
    private String frontEndBaseUrl;

    @GetMapping("/auth/login/oauth2/code/google")
    public void getSessionTokensForOAuthUsers(@RequestParam("code") String authCode, HttpServletResponse response) throws IOException {
        try{
            AuthResponse authResponse = authService.getSessionTokensForOAuthUsers(authCode);
            String accessToken = authResponse.getAccessToken();
            String refreshToken = authResponse.getRefreshToken();
            String redirectUrl = String.format("http://127.0.0.1:5500/html/oauth-success.html?accessToken=%s&refreshToken=%s",
                    URLEncoder.encode(accessToken, StandardCharsets.UTF_8),
                    URLEncoder.encode(refreshToken,StandardCharsets.UTF_8));
            response.sendRedirect(redirectUrl);
            ApiResponse successResponse = new ApiResponse("Authenticated using oAuth2",true,authResponse);
        }catch (Exception e){
            ApiResponse errorResponse = new ApiResponse(e.getMessage(),false,null);
            String errorRedirectUrl = frontEndBaseUrl+"/html/oauth-success.html?error="+URLEncoder.encode(e.getMessage(),StandardCharsets.UTF_8);
            response.sendRedirect(errorRedirectUrl);
        }
    }

}
