package com.store.application.utils.aop;

import org.aspectj.lang.annotation.Pointcut;

public class PointcutDeclarations {
    @Pointcut("execution(public * com.store.application.*.*Controller.*(..))")
    public void controllerMethods() {}

    @Pointcut("execution(public * com.store.application.*.*Service.*(..))")
    public void serviceMethods() {}
}
