package com.microfinance.core_banking.repository.client;

import com.microfinance.core_banking.entity.Client;
import com.microfinance.core_banking.entity.StatutClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    // Recherche unique par code client metier.
    Optional<Client> findByCodeClient(String codeClient);
    // Verification rapide de si un client existe par code client metier.
    boolean existsByCodeClient(String codeClient);

    // Recherche unique par email.
    Optional<Client> findByEmail(String email);
    // Verification rapide de si un client existe par email.
    boolean existsByEmail(String email);

    // Recherche par numéro de téléphone.
    Optional<Client> findByTelephone(String telephone);
    // Verification rapide de si un client existe par numéro de téléphone.
    boolean existsByTelephone(String telephone);

    // Liste paginee des clients par statut.
    Page<Client> findByStatutClient_IdStatutClient(Long idStatutClient, Pageable pageable);

    // Recherche paginee sur le nom ou le prenom.
    Page<Client> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(
            String nom,
            String prenom,
            Pageable pageable
    );

    // Liste paginée des clients inscrits entre deux dates.
    Page<Client> findByCreatedAtBetween(LocalDateTime dateDebut, LocalDateTime dateFin, Pageable pageable);

}
