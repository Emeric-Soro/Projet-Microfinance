package com.soutra.microfinance.repository.credit;

import com.soutra.microfinance.entity.ProduitCredit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProduitCreditRepository extends JpaRepository<ProduitCredit, Long> {

	// Recherche un produit par son code unique.
	Optional<ProduitCredit> findByCodeProduit(String codeProduit);

	// Liste les produits actifs.
	List<ProduitCredit> findByEstActifTrue();

	// Verifie si un code produit existe deja.
	boolean existsByCodeProduit(String codeProduit);

	// Liste paginee des produits credit.
	Page<ProduitCredit> findAllByEstActifTrue(Pageable pageable);
}
