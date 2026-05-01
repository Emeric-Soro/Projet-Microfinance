package com.microfinance.core_banking.repository.credit;

import com.microfinance.core_banking.entity.DemandeCredit;
import com.microfinance.core_banking.entity.StatutDemande;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DemandeCreditRepository extends JpaRepository<DemandeCredit, Long> {

	// Recherche une demande par sa reference unique.
	Optional<DemandeCredit> findByReferenceDemande(String referenceDemande);

	// Liste les demandes d'un client.
	Page<DemandeCredit> findByClient_IdClient(Long idClient, Pageable pageable);

	// Liste les demandes filtrees par statut.
	Page<DemandeCredit> findByStatutDemande(StatutDemande statutDemande, Pageable pageable);

	// Liste les demandes assignees a un agent de credit.
	Page<DemandeCredit> findByAgentCredit_IdUser(Long idAgentCredit, Pageable pageable);
}
