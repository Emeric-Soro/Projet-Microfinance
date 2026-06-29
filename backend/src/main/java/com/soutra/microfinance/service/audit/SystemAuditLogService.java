package com.soutra.microfinance.service.audit;

import com.soutra.microfinance.entity.SystemAuditLog;
import com.soutra.microfinance.repository.audit.SystemAuditLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class SystemAuditLogService {

    private final SystemAuditLogRepository systemAuditLogRepository;

    public SystemAuditLogService(SystemAuditLogRepository systemAuditLogRepository) {
        this.systemAuditLogRepository = systemAuditLogRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void enregistrer(SystemAuditLog auditLog) {
        systemAuditLogRepository.save(auditLog);
    }

    @Transactional(readOnly = true)
    public Page<SystemAuditLog> consulterTous(Pageable pageable) {
        return systemAuditLogRepository.findAllByOrderByDateActionDesc(pageable);
    }

    @Transactional(readOnly = true)
    public Page<SystemAuditLog> consulterParUtilisateur(String utilisateur, Pageable pageable) {
        if (utilisateur == null || utilisateur.isBlank()) {
            return consulterTous(pageable);
        }
        return systemAuditLogRepository.findByUtilisateurOrderByDateActionDesc(utilisateur, pageable);
    }

    @Transactional(readOnly = true)
    public Page<SystemAuditLog> consulterParAction(String action, Pageable pageable) {
        return systemAuditLogRepository.findByActionOrderByDateActionDesc(action, pageable);
    }

    @Transactional(readOnly = true)
    public Page<SystemAuditLog> consulterParPeriode(LocalDateTime debut, LocalDateTime fin, Pageable pageable) {
        return systemAuditLogRepository.findByDateActionBetweenOrderByDateActionDesc(debut, fin, pageable);
    }

    @Transactional(readOnly = true)
    public Page<SystemAuditLog> rechercherAvecFiltres(
            String utilisateur, String action, String ressource, String statut,
            String idEntite, LocalDateTime debut, LocalDateTime fin, Pageable pageable) {
        // Normalise les chaînes vides en null pour que le JPQL IS NULL fonctionne
        return systemAuditLogRepository.rechercherAvecFiltres(
                (utilisateur != null && !utilisateur.isBlank()) ? utilisateur : null,
                (action      != null && !action.isBlank())      ? action      : null,
                (ressource   != null && !ressource.isBlank())   ? ressource   : null,
                (statut      != null && !statut.isBlank())      ? statut      : null,
                (idEntite    != null && !idEntite.isBlank())    ? idEntite    : null,
                debut, fin, pageable
        );
    }
}
