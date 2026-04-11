package com.microfinance.core_banking.repository.communication;

import com.microfinance.core_banking.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Liste paginee des notifications d'un client.
    Page<Notification> findByClient_IdClient(Long idClient, Pageable pageable);

    // Liste paginee des notifications par statut d'envoi.
    Page<Notification> findByStatutEnvoi_IdStatutEnvoi(Long idStatutEnvoi, Pageable pageable);

    // Liste paginee des notifications par canal.
    Page<Notification> findByTypeCanal_IdCanal(Long idCanal, Pageable pageable);

    // Liste paginee des notifications envoyees entre deux dates.
    Page<Notification> findByDateEnvoiBetween(LocalDate dateDebut, LocalDate dateFin, Pageable pageable);

    // Liste paginee des notifications creees entre deux dates.
    Page<Notification> findByCreatedAtBetween(LocalDateTime dateDebut, LocalDateTime dateFin, Pageable pageable);
}
