package com.soutra.microfinance.service.parametrage;

import com.soutra.microfinance.entity.ProduitCredit;
import com.soutra.microfinance.entity.ProduitEpargne;
import com.soutra.microfinance.repository.credit.ProduitCreditRepository;
import com.soutra.microfinance.repository.parametrage.ProduitEpargneRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
	public Page<ProduitCredit> listerProduitsCreditPagine(Pageable pageable) {
		return produitCreditRepository.findAllByEstActifTrue(pageable);
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
	public Page<ProduitEpargne> listerProduitsEpargnePagine(Pageable pageable) {
		return produitEpargneRepository.findAllByEstActifTrue(pageable);
	}

	@Override
	@Transactional(readOnly = true)
	public ProduitEpargne obtenirProduitEpargne(Long idProduit) {
		return produitEpargneRepository.findById(idProduit)
				.orElseThrow(() -> new EntityNotFoundException("Produit d'epargne introuvable: " + idProduit));
	}

	@Override
	@Transactional
	public ProduitCredit modifierProduitCredit(Long idProduit, ProduitCredit modifications) {
		ProduitCredit produit = produitCreditRepository.findById(idProduit)
				.orElseThrow(() -> new EntityNotFoundException("Produit de credit introuvable: " + idProduit));

		if (modifications.getCodeProduit() != null) produit.setCodeProduit(modifications.getCodeProduit());
		if (modifications.getLibelle() != null) produit.setLibelle(modifications.getLibelle());
		if (modifications.getTauxInteretAnnuel() != null) produit.setTauxInteretAnnuel(modifications.getTauxInteretAnnuel());
		if (modifications.getDureeMinMois() != null) produit.setDureeMinMois(modifications.getDureeMinMois());
		if (modifications.getDureeMaxMois() != null) produit.setDureeMaxMois(modifications.getDureeMaxMois());
		if (modifications.getMontantMin() != null) produit.setMontantMin(modifications.getMontantMin());
		if (modifications.getMontantMax() != null) produit.setMontantMax(modifications.getMontantMax());
		if (modifications.getMethodeCalcul() != null) produit.setMethodeCalcul(modifications.getMethodeCalcul());
		if (modifications.getFraisDossierPourcentage() != null) produit.setFraisDossierPourcentage(modifications.getFraisDossierPourcentage());
		if (modifications.getPenaliteRetardPourcentage() != null) produit.setPenaliteRetardPourcentage(modifications.getPenaliteRetardPourcentage());
		if (modifications.getEstActif() != null) produit.setEstActif(modifications.getEstActif());

		return produitCreditRepository.save(produit);
	}

	@Override
	@Transactional
	public void supprimerProduitCredit(Long idProduit) {
		ProduitCredit produit = produitCreditRepository.findById(idProduit)
				.orElseThrow(() -> new EntityNotFoundException("Produit de credit introuvable: " + idProduit));
		produit.setEstActif(false);
		produitCreditRepository.save(produit);
	}

	@Override
	@Transactional
	public ProduitEpargne modifierProduitEpargne(Long idProduit, ProduitEpargne modifications) {
		ProduitEpargne produit = produitEpargneRepository.findById(idProduit)
				.orElseThrow(() -> new EntityNotFoundException("Produit d'epargne introuvable: " + idProduit));

		if (modifications.getCodeProduit() != null) produit.setCodeProduit(modifications.getCodeProduit());
		if (modifications.getLibelle() != null) produit.setLibelle(modifications.getLibelle());
		if (modifications.getTauxInteretAnnuel() != null) produit.setTauxInteretAnnuel(modifications.getTauxInteretAnnuel());
		if (modifications.getMontantMinOuverture() != null) produit.setMontantMinOuverture(modifications.getMontantMinOuverture());
		if (modifications.getPenaliteRetraitAnticipe() != null) produit.setPenaliteRetraitAnticipe(modifications.getPenaliteRetraitAnticipe());
		if (modifications.getDureeMinJours() != null) produit.setDureeMinJours(modifications.getDureeMinJours());
		if (modifications.getEstActif() != null) produit.setEstActif(modifications.getEstActif());

		return produitEpargneRepository.save(produit);
	}

	@Override
	@Transactional
	public void supprimerProduitEpargne(Long idProduit) {
		ProduitEpargne produit = produitEpargneRepository.findById(idProduit)
				.orElseThrow(() -> new EntityNotFoundException("Produit d'epargne introuvable: " + idProduit));
		produit.setEstActif(false);
		produitEpargneRepository.save(produit);
	}
}
