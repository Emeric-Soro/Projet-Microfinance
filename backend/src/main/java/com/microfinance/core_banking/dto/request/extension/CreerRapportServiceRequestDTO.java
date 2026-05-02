package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête service de création d'un rapport")
public class CreerRapportServiceRequestDTO {
    @Schema(description = "Code du rapport (optionnel)", example = "RAP-001")
    private String codeRapport;
    @Schema(description = "Type de rapport (optionnel)", example = "KYC")
    private String typeRapport;
    @Schema(description = "Période du rapport (optionnel)", example = "2026-Q1")
    private String periode;
    @Schema(description = "Statut (optionnel)", example = "BROUILLON")
    private String statut;
    @Schema(description = "Chemin du fichier (optionnel)", example = "/rapports/kyc_2026_q1.pdf")
    private String cheminFichier;
    @Schema(description = "Commentaire (optionnel)", example = "Rapport généré automatiquement")
    private String commentaire;

    private static String required(Map<String, Object> payload, String key) {
        Object value = payload.get(key);
        if (value == null || value.toString().isBlank()) {
            throw new IllegalArgumentException("Le champ '" + key + "' est obligatoire");
        }
        return value.toString().trim();
    }

    public static CreerRapportServiceRequestDTO fromMap(java.util.Map<String, Object> payload) {
        CreerRapportServiceRequestDTO dto = new CreerRapportServiceRequestDTO();
        dto.setCodeRapport((String) payload.get("codeRapport"));
        dto.setTypeRapport((String) payload.get("typeRapport"));
        dto.setPeriode((String) payload.get("periode"));
        dto.setStatut((String) payload.get("statut"));
        dto.setCheminFichier((String) payload.get("cheminFichier"));
        dto.setCommentaire((String) payload.get("commentaire"));
        return dto;
    }
}
