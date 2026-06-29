package com.soutra.microfinance.audit;

import com.soutra.microfinance.entity.SystemAuditLog;
import com.soutra.microfinance.repository.audit.SystemAuditLogRepository;
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

import java.time.LocalDateTime;

@Aspect
@Component
public class AuditLogAspect {

    private static final Logger log = LoggerFactory.getLogger(AuditLogAspect.class);

    private final SystemAuditLogRepository systemAuditLogRepository;

    public AuditLogAspect(SystemAuditLogRepository systemAuditLogRepository) {
        this.systemAuditLogRepository = systemAuditLogRepository;
    }

    @Around("@annotation(auditLog)")
    public Object around(ProceedingJoinPoint joinPoint, AuditLog auditLog) throws Throwable {
        String user = currentUsername();
        String ip = currentClientIp();
        String resource = auditLog.resource().isBlank()
                ? joinPoint.getSignature().toShortString()
                : auditLog.resource();
        String methodSignature = joinPoint.getSignature().toShortString();
        LocalDateTime now = LocalDateTime.now();

        try {
            Object result = joinPoint.proceed();
            log.info(
                    "AUDIT status=SUCCESS user={} action={} resource={} ip={} method={}",
                    user, auditLog.action(), resource, ip, methodSignature
            );
            persisterAudit(user, auditLog.action(), resource, ip, methodSignature, "SUCCESS", null, now);
            return result;
        } catch (Throwable ex) {
            String errorMessage = ex.getClass().getSimpleName()
                    + (ex.getMessage() != null ? ": " + ex.getMessage() : "");
            log.warn(
                    "AUDIT status=FAILURE user={} action={} resource={} ip={} method={} error={}",
                    user, auditLog.action(), resource, ip, methodSignature, ex.getClass().getSimpleName()
            );
            persisterAudit(user, auditLog.action(), resource, ip, methodSignature, "FAILURE", errorMessage, now);
            throw ex;
        }
    }

    private void persisterAudit(String user, String action, String resource, String ip,
                                 String method, String statut, String messageErreur, LocalDateTime date) {
        try {
            AuditContextData ctx = AuditContext.get();
            String finalAction = (ctx != null && ctx.getAction() != null) ? ctx.getAction() : action;
            SystemAuditLog entry = SystemAuditLog.builder()
                    .dateAction(date)
                    .utilisateur(user)
                    .action(finalAction)
                    .ressource(resource)
                    .adresseIp(ip)
                    .methode(method)
                    .statut(statut)
                    .messageErreur(messageErreur != null && messageErreur.length() > 500
                            ? messageErreur.substring(0, 500) : messageErreur)
                    .idEntite(ctx != null ? ctx.getIdEntite() : null)
                    .detailsAvant(ctx != null ? ctx.getDetailsAvant() : null)
                    .detailsApres(ctx != null ? ctx.getDetailsApres() : null)
                    .build();
            systemAuditLogRepository.save(entry);
        } catch (Exception e) {
            log.error("Echec de la persistance de l'audit en base", e);
        } finally {
            AuditContext.clear(); // Toujours nettoyer le ThreadLocal après persistance
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
