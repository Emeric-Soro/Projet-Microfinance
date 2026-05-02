package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête service de génération de bulletin de paie")
public class GenererBulletinPaieServiceRequestDTO {
    @Schema(description = "Identifiant de l'employé (optionnel)", example = "1")
    private String idEmploye;
    @Schema(description = "Période (optionnel)", example = "2026-04")
    private String periode;
    @Schema(description = "Salaire brut (optionnel)", example = "500000.00")
    private BigDecimal salaireBrut;
    @Schema(description = "Retenues (optionnel)", example = "75000.00")
    private BigDecimal retenues;
    @Schema(description = "Salaire net (optionnel)", example = "425000.00")
    private BigDecimal salaireNet;
    @Schema(description = "Statut (optionnel)", example = "BROUILLON")
    private String statut;

    private static String required(Map<String, Object> payload, String key) {
        Object value = payload.get(key);
        if (value == null || value.toString().isBlank()) {
            throw new IllegalArgumentException("Le champ '" + key + "' est obligatoire");
        }
        return value.toString().trim();
    }

    public static GenererBulletinPaieServiceRequestDTO fromMap(java.util.Map<String, Object> payload) {
        GenererBulletinPaieServiceRequestDTO dto = new GenererBulletinPaieServiceRequestDTO();
        dto.setIdEmploye((String) payload.get("idEmploye"));
        dto.setPeriode((String) payload.get("periode"));
        if (payload.get("salaireBrut") != null) dto.setSalaireBrut(new java.math.BigDecimal(payload.get("salaireBrut").toString()));
        if (payload.get("retenues") != null) dto.setRetenues(new java.math.BigDecimal(payload.get("retenues").toString()));
        if (payload.get("salaireNet") != null) dto.setSalaireNet(new java.math.BigDecimal(payload.get("salaireNet").toString()));
        dto.setStatut((String) payload.get("statut"));
        return dto;
    }
}
