package com.example.sns.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
public class AspectOrder {

    @Component
    @Aspect
    @Order(1)
    public static class LogAspect {
        @Around("com.example.sns.aop.PointCuts.all()")
        public Object doLog(ProceedingJoinPoint joinPoint) throws Throwable {
            long startTime = System.currentTimeMillis();
            Object result = joinPoint.proceed();
            long endTime = System.currentTimeMillis();
            log.info("[logging] {} ({}ms)", result, endTime- startTime);
            return result;
        }
    }
}
