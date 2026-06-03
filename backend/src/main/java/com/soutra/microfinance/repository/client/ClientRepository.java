package com.soutra.microfinance.repository.client;

import com.soutra.microfinance.entity.Client;
import com.soutra.microfinance.entity.StatutClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
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

    // Verification rapide de si un client existe avec le meme document d'identite.
    boolean existsByNumeroPieceIdentite(String numeroPieceIdentite);

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

    // --- Aggregation queries for reporting ---

    long countByCreatedAtBetween(LocalDateTime dateDebut, LocalDateTime dateFin);

    @Query("SELECT c.statutClient.libelleStatut, COUNT(c) FROM Client c GROUP BY c.statutClient.libelleStatut")
    List<Object[]> countClientsByStatut();

    @Query("SELECT COUNT(c) FROM Client c WHERE c.agence.idAgence = :agenceId AND c.dateInscription >= :dateDebut")
    long countByAgenceAndDateInscriptionAfter(@Param("agenceId") Long agenceId, @Param("dateDebut") LocalDate dateDebut);

    @Query("SELECT COUNT(c) FROM Client c WHERE c.agence.idAgence = :agenceId")
    long countByAgence(@Param("agenceId") Long agenceId);

    @Query("SELECT COUNT(c) FROM Client c WHERE c.agence.idAgence = :agenceId AND c.statutClient.libelleStatut = :statut")
    long countByAgenceAndStatutLibelle(@Param("agenceId") Long agenceId, @Param("statut") String statut);

    @Query("SELECT COUNT(c) FROM Client c WHERE c.statutClient.libelleStatut = :statut")
    long countByStatutLibelle(@Param("statut") String statut);

    @Query("SELECT COUNT(c) FROM Client c WHERE c.createdAt >= :dateDebut")
    long countByCreatedAtAfter(@Param("dateDebut") LocalDateTime dateDebut);

    @Query("SELECT COUNT(c) FROM Client c WHERE c.dateInscription >= :dateDebut")
    long countByDateInscriptionAfter(@Param("dateDebut") LocalDate dateDebut);

    List<Client> findByDateSoumissionKycBefore(LocalDate dateLimite);
}
