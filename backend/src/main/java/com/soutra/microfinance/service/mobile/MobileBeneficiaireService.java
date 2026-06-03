package com.soutra.microfinance.service.mobile;

import com.soutra.microfinance.dto.response.mobile.MobileBeneficiaireResponseDTO;

import java.util.List;
import java.util.Optional;

public interface MobileBeneficiaireService {

    List<MobileBeneficiaireResponseDTO> listerBeneficiaires(Long idClient);

    MobileBeneficiaireResponseDTO ajouterBeneficiaire(Long idClient, String nom, String prenom, String compteBeneficiaire, String banque);

    MobileBeneficiaireResponseDTO modifierBeneficiaire(Long idBeneficiaire, Long idClient, String nom, String prenom, String compteBeneficiaire, String banque);

    void supprimerBeneficiaire(Long idBeneficiaire, Long idClient);

    Optional<MobileBeneficiaireResponseDTO> trouverParId(Long idBeneficiaire);
}
