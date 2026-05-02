package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête service de création d'un stress test")
public class CreerStressTestServiceRequestDTO {
    @Schema(description = "Code du scénario (optionnel)", example = "SCEN-001")
    private String codeScenario;
    @Schema(description = "Libellé (optionnel)", example = "Scénario crise économique")
    private String libelle;
    @Schema(description = "Taux de défaut (optionnel)", example = "15.00")
    private BigDecimal tauxDefaut;
    @Schema(description = "Taux de retrait (optionnel)", example = "25.00")
    private BigDecimal tauxRetrait;
    @Schema(description = "Statut (optionnel)", example = "ACTIF")
    private String statut;

    private static String required(Map<String, Object> payload, String key) {
        Object value = payload.get(key);
        if (value == null || value.toString().isBlank()) {
            throw new IllegalArgumentException("Le champ '" + key + "' est obligatoire");
        }
        return value.toString().trim();
    }

    public static CreerStressTestServiceRequestDTO fromMap(java.util.Map<String, Object> payload) {
        CreerStressTestServiceRequestDTO dto = new CreerStressTestServiceRequestDTO();
        dto.setCodeScenario((String) payload.get("codeScenario"));
        dto.setLibelle((String) payload.get("libelle"));
        if (payload.get("tauxDefaut") != null) dto.setTauxDefaut(new java.math.BigDecimal(payload.get("tauxDefaut").toString()));
        if (payload.get("tauxRetrait") != null) dto.setTauxRetrait(new java.math.BigDecimal(payload.get("tauxRetrait").toString()));
        dto.setStatut((String) payload.get("statut"));
        return dto;
    }
}
