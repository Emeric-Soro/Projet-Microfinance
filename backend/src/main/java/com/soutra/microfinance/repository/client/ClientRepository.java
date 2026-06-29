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

    @Override
    @Query("SELECT c FROM Client c WHERE c.utilisateur IS NULL OR EXISTS (SELECT r FROM c.utilisateur.roles r WHERE r.codeRoleUtilisateur = 'CLIENT')")
    Page<Client> findAll(Pageable pageable);

    @Override
    @Query("SELECT COUNT(c) FROM Client c WHERE c.utilisateur IS NULL OR EXISTS (SELECT r FROM c.utilisateur.roles r WHERE r.codeRoleUtilisateur = 'CLIENT')")
    long count();

    // Liste paginee des clients par statut.
    @Query("SELECT c FROM Client c WHERE (c.utilisateur IS NULL OR EXISTS (SELECT r FROM c.utilisateur.roles r WHERE r.codeRoleUtilisateur = 'CLIENT')) AND c.statutClient.idStatutClient = :idStatutClient")
    Page<Client> findByStatutClient_IdStatutClient(@Param("idStatutClient") Long idStatutClient, Pageable pageable);

    // Recherche paginee sur le nom ou le prenom.
    @Query("SELECT c FROM Client c WHERE (c.utilisateur IS NULL OR EXISTS (SELECT r FROM c.utilisateur.roles r WHERE r.codeRoleUtilisateur = 'CLIENT')) AND (LOWER(c.nom) LIKE LOWER(CONCAT('%', :nom, '%')) OR LOWER(c.prenom) LIKE LOWER(CONCAT('%', :prenom, '%')))")
    Page<Client> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(
            @Param("nom") String nom,
            @Param("prenom") String prenom,
            Pageable pageable
    );

    @Query("SELECT DISTINCT c FROM Client c " +
           "LEFT JOIN c.comptes co " +
           "WHERE (c.utilisateur IS NULL OR EXISTS (SELECT r FROM c.utilisateur.roles r WHERE r.codeRoleUtilisateur = 'CLIENT')) " +
           "AND (LOWER(c.nom) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(c.prenom) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(c.codeClient) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(c.telephone) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(c.email) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(co.numCompte) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Client> rechercherClientsGlobale(@Param("query") String query, Pageable pageable);

    // Liste paginée des clients inscrits entre deux dates.
    @Query("SELECT c FROM Client c WHERE (c.utilisateur IS NULL OR EXISTS (SELECT r FROM c.utilisateur.roles r WHERE r.codeRoleUtilisateur = 'CLIENT')) AND c.createdAt BETWEEN :dateDebut AND :dateFin")
    Page<Client> findByCreatedAtBetween(@Param("dateDebut") LocalDateTime dateDebut, @Param("dateFin") LocalDateTime dateFin, Pageable pageable);

    // --- Aggregation queries for reporting ---

    @Query("SELECT COUNT(c) FROM Client c WHERE (c.utilisateur IS NULL OR EXISTS (SELECT r FROM c.utilisateur.roles r WHERE r.codeRoleUtilisateur = 'CLIENT')) AND c.createdAt BETWEEN :dateDebut AND :dateFin")
    long countByCreatedAtBetween(@Param("dateDebut") LocalDateTime dateDebut, @Param("dateFin") LocalDateTime dateFin);

    @Query("SELECT c.statutClient.libelleStatut, COUNT(c) FROM Client c WHERE c.utilisateur IS NULL OR EXISTS (SELECT r FROM c.utilisateur.roles r WHERE r.codeRoleUtilisateur = 'CLIENT') GROUP BY c.statutClient.libelleStatut")
    List<Object[]> countClientsByStatut();

    @Query("SELECT COUNT(c) FROM Client c WHERE (c.utilisateur IS NULL OR EXISTS (SELECT r FROM c.utilisateur.roles r WHERE r.codeRoleUtilisateur = 'CLIENT')) AND c.agence.idAgence = :agenceId AND c.dateInscription >= :dateDebut")
    long countByAgenceAndDateInscriptionAfter(@Param("agenceId") Long agenceId, @Param("dateDebut") LocalDate dateDebut);

    @Query("SELECT COUNT(c) FROM Client c WHERE (c.utilisateur IS NULL OR EXISTS (SELECT r FROM c.utilisateur.roles r WHERE r.codeRoleUtilisateur = 'CLIENT')) AND c.agence.idAgence = :agenceId")
    long countByAgence(@Param("agenceId") Long agenceId);

    @Query("SELECT COUNT(c) FROM Client c WHERE (c.utilisateur IS NULL OR EXISTS (SELECT r FROM c.utilisateur.roles r WHERE r.codeRoleUtilisateur = 'CLIENT')) AND c.agence.idAgence = :agenceId AND c.statutClient.libelleStatut = :statut")
    long countByAgenceAndStatutLibelle(@Param("agenceId") Long agenceId, @Param("statut") String statut);

    @Query("SELECT COUNT(c) FROM Client c WHERE (c.utilisateur IS NULL OR EXISTS (SELECT r FROM c.utilisateur.roles r WHERE r.codeRoleUtilisateur = 'CLIENT')) AND c.statutClient.libelleStatut = :statut")
    long countByStatutLibelle(@Param("statut") String statut);

    @Query("SELECT COUNT(c) FROM Client c WHERE (c.utilisateur IS NULL OR EXISTS (SELECT r FROM c.utilisateur.roles r WHERE r.codeRoleUtilisateur = 'CLIENT')) AND c.createdAt >= :dateDebut")
    long countByCreatedAtAfter(@Param("dateDebut") LocalDateTime dateDebut);

    @Query("SELECT COUNT(c) FROM Client c WHERE (c.utilisateur IS NULL OR EXISTS (SELECT r FROM c.utilisateur.roles r WHERE r.codeRoleUtilisateur = 'CLIENT')) AND c.dateInscription >= :dateDebut")
    long countByDateInscriptionAfter(@Param("dateDebut") LocalDate dateDebut);

    @Query("SELECT c FROM Client c WHERE (c.utilisateur IS NULL OR EXISTS (SELECT r FROM c.utilisateur.roles r WHERE r.codeRoleUtilisateur = 'CLIENT')) AND c.dateSoumissionKyc < :dateLimite")
    List<Client> findByDateSoumissionKycBefore(@Param("dateLimite") LocalDate dateLimite);
}
