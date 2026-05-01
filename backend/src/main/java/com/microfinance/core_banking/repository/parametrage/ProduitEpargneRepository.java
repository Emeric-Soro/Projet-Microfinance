package com.microfinance.core_banking.repository.parametrage;

import com.microfinance.core_banking.entity.ProduitEpargne;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProduitEpargneRepository extends JpaRepository<ProduitEpargne, Long> {

	// Recherche un produit par son code unique.
	Optional<ProduitEpargne> findByCodeProduit(String codeProduit);

	// Liste les produits actifs.
	List<ProduitEpargne> findByEstActifTrue();
}
