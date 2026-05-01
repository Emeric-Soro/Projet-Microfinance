package com.microfinance.core_banking.dto.response.extension;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemAuditLogResponseDTO {
    private Long id;
    private String userName;
    private String ipAddress;
    private String correlationId;
    private String userAgent;
    private String roleNames;
    private String agencyCode;
    private String action;
    private String resource;
    private String moduleName;
    private String entityName;
    private String entityId;
    private String requestMethod;
    private String requestPath;
    private LocalDate businessDate;
    private String status;
    private String errorDetails;
    private String reason;
    private String beforeValue;
    private String afterValue;
    private LocalDateTime timestamp;
}
