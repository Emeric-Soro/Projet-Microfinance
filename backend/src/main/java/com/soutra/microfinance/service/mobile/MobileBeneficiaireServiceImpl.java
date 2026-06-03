package com.soutra.microfinance.service.mobile;

import com.soutra.microfinance.dto.response.mobile.MobileBeneficiaireResponseDTO;
import com.soutra.microfinance.entity.Beneficiaire;
import com.soutra.microfinance.service.compte.BeneficiaireService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MobileBeneficiaireServiceImpl implements MobileBeneficiaireService {

    private final BeneficiaireService beneficiaireService;

    @Override
    public List<MobileBeneficiaireResponseDTO> listerBeneficiaires(Long idClient) {
        return beneficiaireService.listerParClient(idClient).stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    public MobileBeneficiaireResponseDTO ajouterBeneficiaire(Long idClient, String nom, String prenom, String compteBeneficiaire, String banque) {
        Beneficiaire saved = beneficiaireService.ajouter(idClient, nom, prenom, compteBeneficiaire, banque);
        return toDTO(saved);
    }

    @Override
    public MobileBeneficiaireResponseDTO modifierBeneficiaire(Long idBeneficiaire, Long idClient, String nom, String prenom, String compteBeneficiaire, String banque) {
        Beneficiaire updated = beneficiaireService.modifier(idBeneficiaire, idClient, nom, prenom, compteBeneficiaire, banque);
        return toDTO(updated);
    }

    @Override
    public void supprimerBeneficiaire(Long idBeneficiaire, Long idClient) {
        beneficiaireService.supprimer(idBeneficiaire, idClient);
    }

    @Override
    public Optional<MobileBeneficiaireResponseDTO> trouverParId(Long idBeneficiaire) {
        return beneficiaireService.trouverParId(idBeneficiaire)
                .map(this::toDTO);
    }

    private MobileBeneficiaireResponseDTO toDTO(Beneficiaire entity) {
        return new MobileBeneficiaireResponseDTO(
                entity.getIdBeneficiaire(),
                entity.getNom(),
                entity.getPrenom(),
                entity.getCompteBeneficiaire(),
                entity.getBanque()
        );
    }
}
