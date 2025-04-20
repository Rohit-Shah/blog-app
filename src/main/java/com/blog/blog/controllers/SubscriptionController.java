package com.blog.blog.controllers;

import com.blog.blog.Response.ApiResponse;
import com.blog.blog.entity.UserPrincipal;
import com.blog.blog.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@RequestMapping("")
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;

        @PostMapping("/users/subscribe/{userId}")
    public ResponseEntity<ApiResponse> subscribe(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long userId){
        try{
            String subscription = subscriptionService.subscribe(userPrincipal,userId);
            ApiResponse successResponse = new ApiResponse("Subscription added",true,subscription);
            return ResponseEntity.status(HttpStatus.OK).body(successResponse);
        }catch (Exception e){
            ApiResponse errorResponse = new ApiResponse(e.getMessage(),false,null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    @PostMapping("/users/unsubscribe/{userId}")
    public ResponseEntity<ApiResponse> unsubscribe(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long userId){
        try{
            String subscription = subscriptionService.unSubscribe(userPrincipal,userId);
            ApiResponse successResponse = new ApiResponse("Subscription added",true,subscription);
            return ResponseEntity.status(HttpStatus.OK).body(successResponse);
        }catch (Exception e){
            ApiResponse errorResponse = new ApiResponse(e.getMessage(),false,null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

}
