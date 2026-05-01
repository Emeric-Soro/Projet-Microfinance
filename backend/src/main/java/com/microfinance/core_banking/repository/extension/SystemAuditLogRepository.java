package com.microfinance.core_banking.repository.extension;

import com.microfinance.core_banking.entity.SystemAuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SystemAuditLogRepository extends JpaRepository<SystemAuditLog, Long> {
    Page<SystemAuditLog> findByUserName(String userName, Pageable pageable);
    List<SystemAuditLog> findByUserName(String userName);
    List<SystemAuditLog> findByAction(String action);
    List<SystemAuditLog> findByStatus(String status);
    List<SystemAuditLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
}
