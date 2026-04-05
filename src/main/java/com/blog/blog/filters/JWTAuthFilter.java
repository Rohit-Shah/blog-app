package com.blog.blog.filters;

import com.blog.blog.entity.UserEntity.UserPrincipal;
import com.blog.blog.service.AuthService.JWTService;
import com.blog.blog.service.serviceBean.AuthService.CustomUserDetailsService;
import com.blog.blog.service.serviceBean.AuthService.JWTServiceBean;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

//now create the filter
@Component
public class JWTAuthFilter extends OncePerRequestFilter {

    private final JWTService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public JWTAuthFilter(JWTService jwtService, CustomUserDetailsService userDetailsService){
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            //as the token is present in headers so extract the token from the header
            String authHeader = request.getHeader("Authorization");
            String token = "";
            String username = "";
            int tokenVersion = 0;
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            token = authHeader.substring(7);
            username = jwtService.extractUsername(token);
            String tokenType = jwtService.extractTokenType(token);
            tokenVersion = jwtService.extractTokenVersion(token);
            // only for access tokens
            if (!"ACCESS".equals(tokenType)) {
                filterChain.doFilter(request, response);
                return;
            }

            Long userId = jwtService.extractUserId(token);
            List<String> roles = jwtService.extractRoles(token);

            //proceed only if the SecurityContextHolder's authentication is empty otherwise it means that the user is
            //already authenticated
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                //as not authenticated at this point authentication needs to be done based on token received
                List<SimpleGrantedAuthority> authorities = roles.stream().map(role -> new SimpleGrantedAuthority("ROLE_"+ role)).toList();
                // as we got the username from the token received from the request headers and
                // now user details are fetched from the username using custom user details service
                // now validate the token using the user details fetched from the db
                if (jwtService.validateToken(token,"ACCESS")) {
                    // now the user is authenticated so pass the flow to UsernamePassword auth filter
                    // so create a new token for that filter
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
                    //used to audit,IP tracking
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    // now set the security context so that next filters can know that this user is already authenticated
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }catch (JwtException e){
            filterChain.doFilter(request,response);
            return;
        }
        // now continue the filter chain
        filterChain.doFilter(request,response);
    }
}
