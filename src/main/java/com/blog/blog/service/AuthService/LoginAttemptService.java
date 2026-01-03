package com.blog.blog.service.AuthService;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class LoginAttemptService {

    private final int MAX_ATTEMPTS = 5;
    private final long BLOCK_TIME_MS = 15 * 60 * 1000;
    private ConcurrentHashMap<String,Attempt> attempts = new ConcurrentHashMap<>();

    public void loginFailed(String username){
        attempts.compute(username,(k,v) -> {
            if(v == null){
                return new Attempt(1,System.currentTimeMillis());
            }
            v.count++;
            v.lastAttempt = System.currentTimeMillis();
            return v;
        });
    }

    public void loginSucceed(String username){
        attempts.remove(username);
    }

    public boolean isBlocked(String username){
        if(!attempts.containsKey(username)){
            return false;
        }
        Attempt userAttempt = attempts.get(username);
        if(userAttempt.count >= MAX_ATTEMPTS){
            if(System.currentTimeMillis() - userAttempt.lastAttempt > BLOCK_TIME_MS){
                attempts.remove(username);
                return false;
            }
            return true;
        }
        return false;
    }

    public long getUserLastLoginDetails(String username){
        if(attempts.get(username) == null) return -1;
        return attempts.get(username).lastAttempt;
    }

    static class Attempt {
        int count;
        long lastAttempt;
        public Attempt(int count,long lastAttempt){
            this.count = count;
            this.lastAttempt = lastAttempt;
        }
    }

}
