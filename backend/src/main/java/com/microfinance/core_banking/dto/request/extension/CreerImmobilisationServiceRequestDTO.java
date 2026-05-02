package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête service de création d'une immobilisation")
public class CreerImmobilisationServiceRequestDTO {
    @Schema(description = "Code de l'immobilisation (optionnel)", example = "IMM-001")
    private String codeImmobilisation;
    @Schema(description = "Libellé (optionnel)", example = "Véhicule de service")
    private String libelle;
    @Schema(description = "Valeur d'origine (optionnel)", example = "25000000.00")
    private BigDecimal valeurOrigine;
    @Schema(description = "Durée d'amortissement en mois (optionnel)", example = "60")
    private String dureeAmortissementMois;
    @Schema(description = "Valeur nette comptable (optionnel)", example = "20000000.00")
    private BigDecimal valeurNette;
    @Schema(description = "Date d'acquisition (optionnel)", example = "2026-01-15")
    private String dateAcquisition;
    @Schema(description = "Statut (optionnel)", example = "ACTIVE")
    private String statut;
    @Schema(description = "Identifiant de l'agence (optionnel)", example = "1")
    private String idAgence;

    private static String required(Map<String, Object> payload, String key) {
        Object value = payload.get(key);
        if (value == null || value.toString().isBlank()) {
            throw new IllegalArgumentException("Le champ '" + key + "' est obligatoire");
        }
        return value.toString().trim();
    }

    public static CreerImmobilisationServiceRequestDTO fromMap(java.util.Map<String, Object> payload) {
        CreerImmobilisationServiceRequestDTO dto = new CreerImmobilisationServiceRequestDTO();
        dto.setCodeImmobilisation((String) payload.get("codeImmobilisation"));
        dto.setLibelle((String) payload.get("libelle"));
        if (payload.get("valeurOrigine") != null) dto.setValeurOrigine(new java.math.BigDecimal(payload.get("valeurOrigine").toString()));
        dto.setDureeAmortissementMois((String) payload.get("dureeAmortissementMois"));
        if (payload.get("valeurNette") != null) dto.setValeurNette(new java.math.BigDecimal(payload.get("valeurNette").toString()));
        dto.setDateAcquisition((String) payload.get("dateAcquisition"));
        dto.setStatut((String) payload.get("statut"));
        dto.setIdAgence((String) payload.get("idAgence"));
        return dto;
    }
}
