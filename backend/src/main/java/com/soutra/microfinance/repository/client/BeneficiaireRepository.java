package com.soutra.microfinance.repository.client;

import com.soutra.microfinance.entity.Beneficiaire;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BeneficiaireRepository extends JpaRepository<Beneficiaire, Long> {

    Page<Beneficiaire> findByIdClient(Long idClient, Pageable pageable);

    List<Beneficiaire> findByIdClientOrderByCreatedAtDesc(Long idClient);

    boolean existsByIdClientAndCompteBeneficiaire(Long idClient, String compteBeneficiaire);
}
