package com.microfinance.core_banking.service.parametrage;

import com.microfinance.core_banking.entity.ProduitCredit;
import com.microfinance.core_banking.entity.ProduitEpargne;
import com.microfinance.core_banking.repository.credit.ProduitCreditRepository;
import com.microfinance.core_banking.repository.parametrage.ProduitEpargneRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProduitServiceImpl implements ProduitService {

	private final ProduitCreditRepository produitCreditRepository;
	private final ProduitEpargneRepository produitEpargneRepository;

	public ProduitServiceImpl(ProduitCreditRepository produitCreditRepository,
							  ProduitEpargneRepository produitEpargneRepository) {
		this.produitCreditRepository = produitCreditRepository;
		this.produitEpargneRepository = produitEpargneRepository;
	}

	@Override
	@Transactional
	public ProduitCredit creerProduitCredit(ProduitCredit produit) {
		if (produitCreditRepository.existsByCodeProduit(produit.getCodeProduit())) {
			throw new IllegalArgumentException("Le code produit '" + produit.getCodeProduit() + "' existe deja.");
		}
		produit.setEstActif(true);
		return produitCreditRepository.save(produit);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProduitCredit> listerProduitsCredit() {
		return produitCreditRepository.findByEstActifTrue();
	}

	@Override
	@Transactional(readOnly = true)
	public ProduitCredit obtenirProduitCredit(Long idProduit) {
		return produitCreditRepository.findById(idProduit)
				.orElseThrow(() -> new EntityNotFoundException("Produit de credit introuvable: " + idProduit));
	}

	@Override
	@Transactional
	public ProduitEpargne creerProduitEpargne(ProduitEpargne produit) {
		produit.setEstActif(true);
		return produitEpargneRepository.save(produit);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProduitEpargne> listerProduitsEpargne() {
		return produitEpargneRepository.findByEstActifTrue();
	}

	@Override
	@Transactional(readOnly = true)
	public ProduitEpargne obtenirProduitEpargne(Long idProduit) {
		return produitEpargneRepository.findById(idProduit)
				.orElseThrow(() -> new EntityNotFoundException("Produit d'epargne introuvable: " + idProduit));
	}
}
