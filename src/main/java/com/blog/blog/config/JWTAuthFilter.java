package com.blog.blog.config;

import com.blog.blog.Exceptions.JWTValidationException;
import com.blog.blog.entity.User;
import com.blog.blog.service.CustomUserDetailsService;
import com.blog.blog.service.JWTService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

//now create the filter
@Component
public class JWTAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JWTService jwtService;
    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            //as the token is present in headers so extract the token from the header
            String authHeader = request.getHeader("Authorization");
            String token = "";
            String username = "";
            if (authHeader == null) {
                filterChain.doFilter(request, response);
                return;
            }
            if (authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
                username = jwtService.extractUsername(token);
            }
            //proceed only if the SecurityContextHolder's authentication is empty otherwise it means that the user is
            //already authenticated
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                //as not authenticated at this point authentication needs to be done based on token received
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                // as we got the username from the token received from the request headers and
                // now user details are fetched from the username using custom user details service
                // now validate the token using the user details fetched from the db
                if (jwtService.validateToken(token, userDetails)) {
                    // now the user is authenticated so pass the flow to UsernamePassword auth filter
                    // so create a new token for that filter
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    // now set the security context so that next filters can know that this user is already authenticated
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }catch (ExpiredJwtException e){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("JWT token expired !! Generate a new token");
        }catch (Exception e){
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.getWriter().write("Some error occurred -> " + e.getMessage());
            throw e;
        }
        // now continue the filter chain
        filterChain.doFilter(request,response);
    }
}
