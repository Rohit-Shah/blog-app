package com.blog.blog.config;

import com.blog.blog.service.serviceBean.AuthService.CustomUserDetailsService;
import com.blog.blog.service.serviceBean.AuthService.JWTServiceBean;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/*
    This class handles the messages that are being sent through web sockets

 */

@Component
public class JWTInterceptor implements ChannelInterceptor {

    private JWTServiceBean jwtServiceBean;
    private CustomUserDetailsService userDetailsService;

    public JWTInterceptor(JWTServiceBean jwtServiceBean, CustomUserDetailsService userDetailsService){
        this.jwtServiceBean = jwtServiceBean;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message,StompHeaderAccessor.class);
        if(StompCommand.CONNECT.equals(accessor.getCommand())){
            String authHeader = accessor.getFirstNativeHeader("Authorization");
            if(authHeader != null && authHeader.startsWith("Bearer ")){
                String token = authHeader.substring(7);
                String username = jwtServiceBean.extractUsername(token);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if(jwtServiceBean.validateToken(token,"ACCESS")){
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                    accessor.setUser(authenticationToken);
                }
            }
        }
        return message;
    }

    @Override
    public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
    }

    @Override
    public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, @Nullable Exception ex) {
    }

    @Override
    public boolean preReceive(MessageChannel channel) {
        return true;
    }

    @Override
    public Message<?> postReceive(Message<?> message, MessageChannel channel) {
        return message;
    }

    @Override
    public void afterReceiveCompletion(@Nullable Message<?> message, MessageChannel channel, @Nullable Exception ex) {
    }

}
