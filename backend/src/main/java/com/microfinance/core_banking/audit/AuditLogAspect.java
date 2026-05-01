package com.microfinance.core_banking.audit;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class AuditLogAspect {

    private static final Logger log = LoggerFactory.getLogger(AuditLogAspect.class);

    @Around("@annotation(auditLog)")
    public Object around(ProceedingJoinPoint joinPoint, AuditLog auditLog) throws Throwable {
        String user = currentUsername();
        String ip = currentClientIp();
        String resource = auditLog.resource().isBlank()
                ? joinPoint.getSignature().toShortString()
                : auditLog.resource();

        try {
            Object result = joinPoint.proceed();
            log.info(
                    "AUDIT status=SUCCESS user={} action={} resource={} ip={} method={}",
                    user,
                    auditLog.action(),
                    resource,
                    ip,
                    joinPoint.getSignature().toShortString()
            );
            return result;
        } catch (Throwable ex) {
            log.warn(
                    "AUDIT status=FAILURE user={} action={} resource={} ip={} method={} error={}",
                    user,
                    auditLog.action(),
                    resource,
                    ip,
                    joinPoint.getSignature().toShortString(),
                    ex.getClass().getSimpleName()
            );
            throw ex;
        }
    }

    private String currentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            return "ANONYMOUS";
        }
        return authentication.getName();
    }

    private String currentClientIp() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            return "N/A";
        }
        HttpServletRequest request = attrs.getRequest();
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
