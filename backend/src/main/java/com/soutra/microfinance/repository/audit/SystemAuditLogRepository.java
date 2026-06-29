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

    @org.springframework.data.jpa.repository.Query(
        "SELECT l FROM SystemAuditLog l WHERE " +
        "(:utilisateur IS NULL OR l.utilisateur = :utilisateur) AND " +
        "(:action     IS NULL OR l.action      = :action)      AND " +
        "(:ressource  IS NULL OR l.ressource   = :ressource)   AND " +
        "(:statut     IS NULL OR l.statut      = :statut)      AND " +
        "(:idEntite   IS NULL OR l.idEntite    = :idEntite)    AND " +
        "(:debut      IS NULL OR l.dateAction >= :debut)       AND " +
        "(:fin        IS NULL OR l.dateAction <= :fin)         " +
        "ORDER BY l.dateAction DESC"
    )
    Page<SystemAuditLog> rechercherAvecFiltres(
        @org.springframework.data.repository.query.Param("utilisateur") String utilisateur,
        @org.springframework.data.repository.query.Param("action")      String action,
        @org.springframework.data.repository.query.Param("ressource")   String ressource,
        @org.springframework.data.repository.query.Param("statut")      String statut,
        @org.springframework.data.repository.query.Param("idEntite")    String idEntite,
        @org.springframework.data.repository.query.Param("debut")       java.time.LocalDateTime debut,
        @org.springframework.data.repository.query.Param("fin")         java.time.LocalDateTime fin,
        Pageable pageable
    );
}
