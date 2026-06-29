package com.soutra.microfinance.service.compte;

import com.soutra.microfinance.entity.Beneficiaire;
import com.soutra.microfinance.repository.client.BeneficiaireRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import com.soutra.microfinance.audit.AuditContext;

@Service
public class BeneficiaireServiceImpl implements BeneficiaireService {

    private final BeneficiaireRepository beneficiaireRepository;

    public BeneficiaireServiceImpl(BeneficiaireRepository beneficiaireRepository) {
        this.beneficiaireRepository = beneficiaireRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Beneficiaire> listerParClient(Long idClient) {
        return beneficiaireRepository.findByIdClientOrderByCreatedAtDesc(idClient);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Beneficiaire> listerTous() {
        return beneficiaireRepository.findAll();
    }

    @Override
    @Transactional
    public Beneficiaire ajouter(Long idClient, String nom, String prenom, String compteBeneficiaire, String banque) {
        validerChamps(nom, prenom, compteBeneficiaire);
        if (beneficiaireRepository.existsByIdClientAndCompteBeneficiaire(idClient, compteBeneficiaire)) {
            throw new IllegalArgumentException("Un beneficiaire avec ce compte existe deja pour ce client");
        }
        Beneficiaire beneficiaire = new Beneficiaire();
        beneficiaire.setIdClient(idClient);
        beneficiaire.setNom(nom.trim());
        beneficiaire.setPrenom(prenom == null ? null : prenom.trim());
        beneficiaire.setCompteBeneficiaire(compteBeneficiaire.trim());
        beneficiaire.setBanque(banque == null ? null : banque.trim());
        return beneficiaireRepository.save(beneficiaire);
    }

    @Override
    @Transactional
    public Beneficiaire modifier(Long idBeneficiaire, Long idClient, String nom, String prenom, String compteBeneficiaire, String banque) {
        validerChamps(nom, prenom, compteBeneficiaire);
        Beneficiaire beneficiaire = chargerBeneficiaireDuClient(idBeneficiaire, idClient);

        AuditContext.setIdEntite(String.valueOf(idBeneficiaire));
        java.util.Map<String, Object> avant = new java.util.HashMap<>();
        avant.put("nom", beneficiaire.getNom());
        avant.put("prenom", beneficiaire.getPrenom());
        avant.put("compteBeneficiaire", beneficiaire.getCompteBeneficiaire());
        avant.put("banque", beneficiaire.getBanque());
        AuditContext.setDetailsAvant(AuditContext.toJson(avant));

        beneficiaire.setNom(nom.trim());
        beneficiaire.setPrenom(prenom == null ? null : prenom.trim());
        beneficiaire.setCompteBeneficiaire(compteBeneficiaire.trim());
        beneficiaire.setBanque(banque == null ? null : banque.trim());
        Beneficiaire saved = beneficiaireRepository.save(beneficiaire);

        java.util.Map<String, Object> apres = new java.util.HashMap<>();
        apres.put("nom", saved.getNom());
        apres.put("prenom", saved.getPrenom());
        apres.put("compteBeneficiaire", saved.getCompteBeneficiaire());
        apres.put("banque", saved.getBanque());
        AuditContext.setDetailsApres(AuditContext.toJson(apres));

        return saved;
    }

    @Override
    @Transactional
    public void supprimer(Long idBeneficiaire, Long idClient) {
        Beneficiaire beneficiaire = chargerBeneficiaireDuClient(idBeneficiaire, idClient);

        AuditContext.setIdEntite(String.valueOf(idBeneficiaire));
        java.util.Map<String, Object> avant = new java.util.HashMap<>();
        avant.put("nom", beneficiaire.getNom());
        avant.put("prenom", beneficiaire.getPrenom());
        avant.put("compteBeneficiaire", beneficiaire.getCompteBeneficiaire());
        avant.put("banque", beneficiaire.getBanque());
        AuditContext.setDetailsAvant(AuditContext.toJson(avant));

        beneficiaireRepository.delete(beneficiaire);

        java.util.Map<String, Object> apres = new java.util.HashMap<>();
        apres.put("deleted", true);
        AuditContext.setDetailsApres(AuditContext.toJson(apres));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Beneficiaire> trouverParId(Long idBeneficiaire) {
        return beneficiaireRepository.findById(idBeneficiaire);
    }

    private Beneficiaire chargerBeneficiaireDuClient(Long idBeneficiaire, Long idClient) {
        Beneficiaire beneficiaire = beneficiaireRepository.findById(idBeneficiaire)
                .orElseThrow(() -> new EntityNotFoundException("Beneficiaire introuvable: " + idBeneficiaire));
        if (!beneficiaire.getIdClient().equals(idClient)) {
            throw new EntityNotFoundException("Beneficiaire introuvable: " + idBeneficiaire);
        }
        return beneficiaire;
    }

    private void validerChamps(String nom, String prenom, String compteBeneficiaire) {
        if (nom == null || nom.isBlank()) {
            throw new IllegalArgumentException("Le nom est obligatoire");
        }
        if (prenom == null || prenom.isBlank()) {
            throw new IllegalArgumentException("Le prenom est obligatoire");
        }
        if (compteBeneficiaire == null || compteBeneficiaire.isBlank()) {
            throw new IllegalArgumentException("Le compte beneficiaire est obligatoire");
        }
    }
}
