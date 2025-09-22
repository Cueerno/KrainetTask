package com.radiuk.auth_service.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class ExceptionLoggingAspect {

    @AfterThrowing(
            pointcut = "within(@org.springframework.web.bind.annotation.RestControllerAdvice *)",
            throwing = "ex"
    )
    public void logException(JoinPoint joinPoint, Throwable ex) {
        String methodName = joinPoint.getSignature().toShortString();
        log.error("[ExceptionHandler] -> {} handled exception: {}",
                methodName, ex.getMessage(), ex);
    }
}