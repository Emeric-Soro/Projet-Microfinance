package com.soutra.microfinance.service.parametrage;

import com.soutra.microfinance.entity.ProduitCredit;
import com.soutra.microfinance.entity.ProduitEpargne;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

// Interface du service de gestion des produits (credits et epargne).
public interface ProduitService {
	ProduitCredit creerProduitCredit(ProduitCredit produit);
	List<ProduitCredit> listerProduitsCredit();
	ProduitCredit obtenirProduitCredit(Long idProduit);

	Page<ProduitCredit> listerProduitsCreditPagine(Pageable pageable);

	ProduitEpargne creerProduitEpargne(ProduitEpargne produit);
	List<ProduitEpargne> listerProduitsEpargne();
	ProduitEpargne obtenirProduitEpargne(Long idProduit);

	Page<ProduitEpargne> listerProduitsEpargnePagine(Pageable pageable);

	ProduitCredit modifierProduitCredit(Long idProduit, ProduitCredit modifications);

	void supprimerProduitCredit(Long idProduit);

	ProduitEpargne modifierProduitEpargne(Long idProduit, ProduitEpargne modifications);

	void supprimerProduitEpargne(Long idProduit);
}
