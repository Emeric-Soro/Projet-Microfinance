package com.soutra.microfinance.service.parametrage;

import com.soutra.microfinance.entity.ProduitCredit;
import com.soutra.microfinance.entity.ProduitEpargne;
import java.util.List;

// Interface du service de gestion des produits (credits et epargne).
public interface ProduitService {
	ProduitCredit creerProduitCredit(ProduitCredit produit);
	List<ProduitCredit> listerProduitsCredit();
	ProduitCredit obtenirProduitCredit(Long idProduit);

	ProduitEpargne creerProduitEpargne(ProduitEpargne produit);
	List<ProduitEpargne> listerProduitsEpargne();
	ProduitEpargne obtenirProduitEpargne(Long idProduit);
}
