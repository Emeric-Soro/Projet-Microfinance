package com.soutra.microfinance.dto.response.compte;

import com.soutra.microfinance.entity.Beneficiaire;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BeneficiaireResponseDTO {

    private Long idBeneficiaire;
    private Long idClient;
    private String nom;
    private String prenom;
    private String compteBeneficiaire;
    private String banque;
    private LocalDateTime dateCreation;

    public static BeneficiaireResponseDTO fromEntity(Beneficiaire entity) {
        if (entity == null) {
            return null;
        }
        return BeneficiaireResponseDTO.builder()
                .idBeneficiaire(entity.getIdBeneficiaire())
                .idClient(entity.getIdClient())
                .nom(entity.getNom())
                .prenom(entity.getPrenom())
                .compteBeneficiaire(entity.getCompteBeneficiaire())
                .banque(entity.getBanque())
                .dateCreation(entity.getCreatedAt())
                .build();
    }
}
