package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête service de consultation BIC")
public class ConsulterBicServiceRequestDTO {
    @Schema(description = "Identifiant du client (optionnel)", example = "1")
    private String idClient;
    @Schema(description = "Encours externe (optionnel)", example = "500000.00")
    private BigDecimal encoursExterne;
    @Schema(description = "Code du rapport (optionnel)", example = "BIC-001")
    private String codeRapport;
    @Schema(description = "Période (optionnel)", example = "2026-Q1")
    private String periode;
    @Schema(description = "Statut (optionnel)", example = "GENERE")
    private String statut;
    @Schema(description = "Référence de consentement (optionnel)", example = "CONS-001")
    private String referenceConsentement;
    @Schema(description = "Nombre d'établissements (optionnel)", example = "5")
    private String nombreEtablissements;

    private static String required(Map<String, Object> payload, String key) {
        Object value = payload.get(key);
        if (value == null || value.toString().isBlank()) {
            throw new IllegalArgumentException("Le champ '" + key + "' est obligatoire");
        }
        return value.toString().trim();
    }

    public static ConsulterBicServiceRequestDTO fromMap(java.util.Map<String, Object> payload) {
        ConsulterBicServiceRequestDTO dto = new ConsulterBicServiceRequestDTO();
        dto.setIdClient((String) payload.get("idClient"));
        if (payload.get("encoursExterne") != null) dto.setEncoursExterne(new java.math.BigDecimal(payload.get("encoursExterne").toString()));
        dto.setCodeRapport((String) payload.get("codeRapport"));
        dto.setPeriode((String) payload.get("periode"));
        dto.setStatut((String) payload.get("statut"));
        dto.setReferenceConsentement((String) payload.get("referenceConsentement"));
        dto.setNombreEtablissements((String) payload.get("nombreEtablissements"));
        return dto;
    }
}
