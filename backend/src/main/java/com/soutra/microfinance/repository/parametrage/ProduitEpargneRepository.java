package com.soutra.microfinance.repository.parametrage;

import com.soutra.microfinance.entity.ProduitEpargne;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

	// Liste paginee des produits epargne actifs.
	Page<ProduitEpargne> findAllByEstActifTrue(Pageable pageable);
}
