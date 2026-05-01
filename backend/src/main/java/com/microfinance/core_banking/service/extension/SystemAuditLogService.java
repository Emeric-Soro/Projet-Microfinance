package com.microfinance.core_banking.service.extension;

import com.microfinance.core_banking.entity.SystemAuditLog;
import com.microfinance.core_banking.repository.extension.SystemAuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SystemAuditLogService {

    private final SystemAuditLogRepository auditLogRepository;

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logAudit(SystemAuditLog auditLog) {
        auditLogRepository.save(auditLog);
    }
    
    @Transactional(readOnly = true)
    public Page<SystemAuditLog> getAuditLogs(Pageable pageable) {
        return auditLogRepository.findAll(pageable);
    }
    
    @Transactional(readOnly = true)
    public Page<SystemAuditLog> getAuditLogsByUser(String userName, Pageable pageable) {
        return auditLogRepository.findByUserName(userName, pageable);
    }
}
