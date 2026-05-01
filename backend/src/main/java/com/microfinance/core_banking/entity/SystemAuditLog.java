package com.microfinance.core_banking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "system_audit_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_name", nullable = false)
    private String userName;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "correlation_id")
    private String correlationId;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "role_names", length = 500)
    private String roleNames;

    @Column(name = "agency_code", length = 50)
    private String agencyCode;

    @Column(name = "action", nullable = false)
    private String action;

    @Column(name = "resource_name")
    private String resource;

    @Column(name = "module_name", length = 100)
    private String moduleName;

    @Column(name = "entity_name", length = 100)
    private String entityName;

    @Column(name = "entity_id", length = 120)
    private String entityId;

    @Column(name = "request_method", length = 20)
    private String requestMethod;

    @Column(name = "request_path", length = 255)
    private String requestPath;

    @Column(name = "business_date")
    private java.time.LocalDate businessDate;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "error_details", columnDefinition = "TEXT")
    private String errorDetails;

    @Column(name = "reason", length = 500)
    private String reason;

    @Column(name = "before_value", columnDefinition = "CLOB")
    private String beforeValue;

    @Column(name = "after_value", columnDefinition = "CLOB")
    private String afterValue;

    @Column(name = "event_timestamp", nullable = false)
    private LocalDateTime timestamp;

    @PrePersist
    protected void onCreate() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }
}
