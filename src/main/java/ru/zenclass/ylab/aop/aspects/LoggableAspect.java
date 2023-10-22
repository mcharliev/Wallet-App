package ru.zenclass.ylab.aop.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aspect
public class LoggableAspect {
    Logger log = LoggerFactory.getLogger(LoggableAspect.class);
    @Pointcut("within(@ru.zenclass.ylab.aop.annotations.Loggable *) && execution(* *(..))")
    public void annotatedByLoggable() {

    }

    @Around("annotatedByLoggable()")
    public Object logging(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        log.info("Calling method " + proceedingJoinPoint.getSignature());
        System.out.println("Calling method " + proceedingJoinPoint.getSignature());
        long startTime = System.currentTimeMillis();
        Object result = proceedingJoinPoint.proceed();
        long endTime = System.currentTimeMillis();
        log.info("Calling method " + proceedingJoinPoint.getSignature());
        System.out.println("Execution of method " + proceedingJoinPoint.getSignature() +
                " finished. Execution time is " + (endTime - startTime) + " ms");
        return result;
    }
}
