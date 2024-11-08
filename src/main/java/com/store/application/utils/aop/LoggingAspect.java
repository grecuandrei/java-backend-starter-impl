package com.store.application.utils.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Order(1)
@Slf4j
@Component
public class LoggingAspect {
    @Around("com.store.application.utils.aop.PointcutDeclarations.controllerMethods()")
    public Object logAroundController(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        log.info("Entering Controller Method: {} with arguments: {}", signature.getMethod().getName(), Arrays.toString(joinPoint.getArgs()));
        try {
            Object result = joinPoint.proceed();
            log.info("Exiting Controller Method: {} with result: {}", signature.getMethod().getName(), result);
            return result;
        } catch (Throwable throwable) {
            log.error("Exception in Controller Method: {} with message: {}", signature.getMethod().getName(), throwable.getMessage());
            throw throwable;
        }
    }

    @Around("com.store.application.utils.aop.PointcutDeclarations.serviceMethods()")
    public Object logAroundService(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        log.info("Entering Service Method: {} with arguments: {}", signature.getMethod().getName(), Arrays.toString(joinPoint.getArgs()));
        try {
            Object result = joinPoint.proceed();
            log.info("Exiting Service Method: {} with result: {}", signature.getMethod().getName(), result);
            return result;
        } catch (Throwable throwable) {
            log.error("Exception in Service Method: {} with message: {}", signature.getMethod().getName(), throwable.getMessage());
            throw throwable;
        }
    }
}
