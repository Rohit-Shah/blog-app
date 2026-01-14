package com.blog.blog.aspect;

import com.blog.blog.annotations.LogUserAction;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
public class LoggingAspect {

    @Around("execution(* com.blog.blog.service.AuthService.AuthService.*(..))")
    public Object logApiExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        log.info("Entering service {} at {}",joinPoint.getSignature().getName(),startTime);
        Object response = joinPoint.proceed();
        long endTime = System.currentTimeMillis();
        log.info("Exiting service {} at {}",joinPoint.getSignature().getName(),endTime);
        log.info("Total time taken {}",endTime - startTime);
        return response;
    }

    //Exceptions
    @AfterThrowing(
            pointcut = "execution(* com.blog.blog.service..*.*(..))",
            throwing = "ex"
    )
    public Object logExceptions(ProceedingJoinPoint joinPoint,Exception ex) throws Throwable {
        log.error("Exception occurred while executing {} service : {}",joinPoint.getSignature().getName(),ex.getMessage());
        Object response = joinPoint.proceed();
        return response;
    }

    //user actions (custom annotation)
    @After("@annotation(logUserAction)")
    public void logUserActions(JoinPoint joinPoint,LogUserAction logUserAction){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("User {} performed action {}",username,logUserAction.actionType());
    }

    //DB calls
    @Around("within(@org.springframework.stereotype.Repository *)")
    public Object logRepositoryTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object response = joinPoint.proceed();
        long endTime = System.currentTimeMillis();
        if(endTime - startTime > 500){
            log.info("{} DB call took more time - {}", joinPoint.getSignature().getName(),endTime-startTime);
        }
        return response;
    }

}