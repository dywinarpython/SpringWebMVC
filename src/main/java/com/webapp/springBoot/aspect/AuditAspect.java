package com.webapp.springBoot.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class AuditAspect {

    @Pointcut("within(com.webapp.springBoot.service..*)")
//    @Pointcut("bean(*Service)")
    public void auditAspect(){}

    @Around("auditAspect()")
    public Object logExecutionTime(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        String signature = proceedingJoinPoint.getSignature().getName();
        String className = proceedingJoinPoint.getSignature().getDeclaringTypeName();
        long startTime = System.currentTimeMillis();
        Object result =  proceedingJoinPoint.proceed();
        long endTime = System.currentTimeMillis();
        log.info("[AUDIT] Метод: {}.{} был выполнен за {} мс", className, signature, endTime-startTime );
        return result;
    }

}
