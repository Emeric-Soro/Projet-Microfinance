package com.soutra.microfinance.service.compte;

import com.soutra.microfinance.entity.Beneficiaire;

import java.util.List;
import java.util.Optional;

public interface BeneficiaireService {

    List<Beneficiaire> listerParClient(Long idClient);

    Beneficiaire ajouter(Long idClient, String nom, String prenom, String compteBeneficiaire, String banque);

    Beneficiaire modifier(Long idBeneficiaire, Long idClient, String nom, String prenom, String compteBeneficiaire, String banque);

    void supprimer(Long idBeneficiaire, Long idClient);

    Optional<Beneficiaire> trouverParId(Long idBeneficiaire);
}
