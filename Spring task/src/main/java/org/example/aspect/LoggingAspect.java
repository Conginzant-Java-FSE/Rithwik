package org.example.aspect;

import org.aspectj.lang.JoinPoint;

import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

// Aspect class for logging UserDao operations
@Aspect
@Component
public class LoggingAspect {

    @Pointcut("execution(* org.example.dao.UserDaoImpl.*(..))")
    public void userDaoMethods() {
    }

    @Before("userDaoMethods()")
    public void beforeAdvice(JoinPoint joinPoint) {
        System.out.println("[AOP - BEFORE] Calling method: " + joinPoint.getSignature().getName());
    }

    @After("userDaoMethods()")
    public void afterAdvice(JoinPoint joinPoint) {
        System.out.println("[AOP - AFTER] Completed method: " + joinPoint.getSignature().getName());
    }

    @AfterReturning(pointcut = "userDaoMethods()", returning = "result")
    public void afterReturningAdvice(JoinPoint joinPoint, Object result) {
        System.out.println("[AOP - AFTER RETURNING] Method: " + joinPoint.getSignature().getName()
                + " returned: " + result);
    }

    @AfterThrowing(pointcut = "userDaoMethods()", throwing = "exception")
    public void afterThrowingAdvice(JoinPoint joinPoint, Exception exception) {
        System.out.println("[AOP - AFTER THROWING] Method: " + joinPoint.getSignature().getName()
                + " threw: " + exception.getMessage());
    }

}
