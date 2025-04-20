package com.blog.blog.Response;

import lombok.Data;

@Data
public class ApiResponse {
    private String message;
    private boolean success;
    private Object data;

    public ApiResponse(String message,boolean success,Object data){
        this.message = message;
        this.success = success;
        this.data = data;
    }
}
