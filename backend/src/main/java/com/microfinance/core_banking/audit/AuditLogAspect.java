package com.microfinance.core_banking.audit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microfinance.core_banking.config.RequestCorrelationFilter;
import com.microfinance.core_banking.entity.RoleUtilisateur;
import com.microfinance.core_banking.entity.SystemAuditLog;
import com.microfinance.core_banking.entity.Utilisateur;
import com.microfinance.core_banking.service.extension.SystemAuditLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
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

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Aspect
@Component
@RequiredArgsConstructor
public class AuditLogAspect {

    private static final Logger log = LoggerFactory.getLogger(AuditLogAspect.class);
    
    private final SystemAuditLogService auditLogService;
    private final ObjectMapper objectMapper;

    @Around("@annotation(auditLog)")
    public Object around(ProceedingJoinPoint joinPoint, AuditLog auditLog) throws Throwable {
        String user = currentUsername();
        String ip = currentClientIp();
        String correlationId = currentCorrelationId();
        String userAgent = currentUserAgent();
        String requestMethod = currentRequestMethod();
        String requestPath = currentRequestPath();
        String resource = auditLog.resource().isBlank()
                ? joinPoint.getSignature().toShortString()
                : auditLog.resource();
        String moduleName = extractModule(joinPoint);
        String entityName = extractEntity(resource);
        String entityId = extractEntityId(joinPoint.getArgs());
        String reason = extractReason(joinPoint.getArgs());
        String payloadSnapshot = serializeArguments(joinPoint.getArgs());
        String roleNames = currentRoleNames();
        String agencyCode = currentAgencyCode();

        try {
            Object result = joinPoint.proceed();
            
            // Log to console
            log.info(
                    "AUDIT status=SUCCESS user={} action={} resource={} ip={} method={}",
                    user,
                    auditLog.action(),
                    resource,
                    ip,
                    correlationId,
                    joinPoint.getSignature().toShortString()
            );
            
            // Log to database
            auditLogService.logAudit(SystemAuditLog.builder()
                    .userName(user)
                    .correlationId(correlationId)
                    .userAgent(userAgent)
                    .roleNames(roleNames)
                    .agencyCode(agencyCode)
                    .action(auditLog.action())
                    .resource(resource)
                    .moduleName(moduleName)
                    .entityName(entityName)
                    .entityId(entityId)
                    .ipAddress(ip)
                    .requestMethod(requestMethod)
                    .requestPath(requestPath)
                    .businessDate(LocalDate.now())
                    .status("SUCCESS")
                    .reason(reason)
                    .afterValue(payloadSnapshot)
                    .timestamp(LocalDateTime.now())
                    .build());
                    
            return result;
        } catch (Throwable ex) {
            // Log to console
            log.warn(
                    "AUDIT status=FAILURE user={} action={} resource={} ip={} method={} error={}",
                    user,
                    auditLog.action(),
                    resource,
                    ip,
                    correlationId,
                    joinPoint.getSignature().toShortString(),
                    ex.getClass().getSimpleName()
            );
            
            // Log to database
            auditLogService.logAudit(SystemAuditLog.builder()
                    .userName(user)
                    .correlationId(correlationId)
                    .userAgent(userAgent)
                    .roleNames(roleNames)
                    .agencyCode(agencyCode)
                    .action(auditLog.action())
                    .resource(resource)
                    .moduleName(moduleName)
                    .entityName(entityName)
                    .entityId(entityId)
                    .ipAddress(ip)
                    .requestMethod(requestMethod)
                    .requestPath(requestPath)
                    .businessDate(LocalDate.now())
                    .status("FAILURE")
                    .reason(reason)
                    .afterValue(payloadSnapshot)
                    .errorDetails(ex.getMessage())
                    .timestamp(LocalDateTime.now())
                    .build());
                    
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

    private String currentCorrelationId() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            return "N/A";
        }
        Object correlationId = attrs.getRequest().getAttribute(RequestCorrelationFilter.CORRELATION_ID_ATTRIBUTE);
        return correlationId == null ? "N/A" : correlationId.toString();
    }

    private String currentUserAgent() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attrs == null ? "N/A" : String.valueOf(attrs.getRequest().getHeader("User-Agent"));
    }

    private String currentRequestMethod() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attrs == null ? "N/A" : attrs.getRequest().getMethod();
    }

    private String currentRequestPath() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attrs == null ? "N/A" : attrs.getRequest().getRequestURI();
    }

    private String currentRoleNames() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Utilisateur utilisateur)) {
            return null;
        }
        return utilisateur.getRoles().stream()
                .map(RoleUtilisateur::getCodeRoleUtilisateur)
                .sorted()
                .collect(Collectors.joining(","));
    }

    private String currentAgencyCode() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Utilisateur utilisateur)) {
            return null;
        }
        return utilisateur.getAgenceActive() == null ? null : utilisateur.getAgenceActive().getCodeAgence();
    }

    private String extractModule(ProceedingJoinPoint joinPoint) {
        Package packageName = joinPoint.getTarget().getClass().getPackage();
        if (packageName == null) {
            return "UNKNOWN";
        }
        String[] tokens = packageName.getName().split("\\.");
        return tokens.length == 0 ? packageName.getName() : tokens[tokens.length - 1].toUpperCase();
    }

    private String extractEntity(String resource) {
        if (resource == null || resource.isBlank()) {
            return null;
        }
        String[] tokens = resource.split("[/:._-]");
        return tokens.length == 0 ? resource : tokens[tokens.length - 1].toUpperCase();
    }

    private String extractEntityId(Object[] args) {
        for (Object arg : args) {
            if (arg instanceof Number || arg instanceof CharSequence) {
                return arg.toString();
            }
            if (arg instanceof Map<?, ?> map) {
                Object id = map.get("id");
                if (id == null) {
                    id = map.entrySet().stream()
                            .filter(entry -> entry.getKey() != null && entry.getKey().toString().toLowerCase().startsWith("id"))
                            .map(Map.Entry::getValue)
                            .findFirst()
                            .orElse(null);
                }
                if (id != null) {
                    return id.toString();
                }
            }
            String reflectedId = invokeGetterIfPresent(arg, "getId");
            if (reflectedId != null) {
                return reflectedId;
            }
        }
        return null;
    }

    private String extractReason(Object[] args) {
        for (Object arg : args) {
            if (arg instanceof Map<?, ?> map) {
                for (String key : new String[]{"motif", "reason", "commentaire", "commentaireChecker", "commentaireMaker"}) {
                    Object value = map.get(key);
                    if (value != null && !value.toString().isBlank()) {
                        return value.toString();
                    }
                }
            }
            for (String methodName : new String[]{"getMotif", "getReason", "getCommentaire"}) {
                String reflected = invokeGetterIfPresent(arg, methodName);
                if (reflected != null && !reflected.isBlank()) {
                    return reflected;
                }
            }
        }
        return null;
    }

    private String serializeArguments(Object[] args) {
        Map<String, Object> serializable = new LinkedHashMap<>();
        int index = 0;
        for (Object arg : args) {
            if (arg == null || arg instanceof HttpServletRequest) {
                continue;
            }
            serializable.put("arg" + index++, arg);
        }
        try {
            return serializable.isEmpty() ? null : objectMapper.writeValueAsString(serializable);
        } catch (JsonProcessingException exception) {
            return Arrays.toString(args);
        }
    }

    private String invokeGetterIfPresent(Object target, String methodName) {
        if (target == null) {
            return null;
        }
        try {
            Method method = target.getClass().getMethod(methodName);
            Object value = method.invoke(target);
            return value == null ? null : value.toString();
        } catch (ReflectiveOperationException ignored) {
            return null;
        }
    }
}
