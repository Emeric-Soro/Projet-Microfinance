package com.soutra.microfinance.repository.audit;

import com.soutra.microfinance.entity.SystemAuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface SystemAuditLogRepository extends JpaRepository<SystemAuditLog, Long> {

    Page<SystemAuditLog> findAllByOrderByDateActionDesc(Pageable pageable);

    Page<SystemAuditLog> findByUtilisateurOrderByDateActionDesc(String utilisateur, Pageable pageable);

    Page<SystemAuditLog> findByActionOrderByDateActionDesc(String action, Pageable pageable);

    Page<SystemAuditLog> findByDateActionBetweenOrderByDateActionDesc(
            LocalDateTime debut, LocalDateTime fin, Pageable pageable);

    Page<SystemAuditLog> findByStatutOrderByDateActionDesc(String statut, Pageable pageable);
}
