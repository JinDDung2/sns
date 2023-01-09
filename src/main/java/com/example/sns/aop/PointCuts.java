package com.example.sns.aop;

import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
public class PointCuts {

    // com.example.sns 하위 패키지
    @Pointcut("execution(* com.example.sns..*(..)))")
    public void all() {}
}
