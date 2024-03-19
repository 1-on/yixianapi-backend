package com.yixian.yixianapiinterface.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * 调用次数切面
 */
@Aspect
@Component
public class LogAOP {

    @After("execution(* com.yixian.yixianapiinterface.controller.NameController.*(..))")
    public void logAfterControllerMethods(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        System.out.println("方法执行：" + methodName);
    }
}
