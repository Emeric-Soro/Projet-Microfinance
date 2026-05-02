package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête service de création d'un budget")
public class CreerBudgetServiceRequestDTO {
    @Schema(description = "Code du budget (optionnel)", example = "BUD-2026-001")
    private String codeBudget;
    @Schema(description = "Année budgétaire (optionnel)", example = "2026")
    private String annee;
    @Schema(description = "Montant total (optionnel)", example = "50000000.00")
    private BigDecimal montantTotal;
    @Schema(description = "Statut (optionnel)", example = "BROUILLON")
    private String statut;
    @Schema(description = "Identifiant de l'agence (optionnel)", example = "1")
    private String idAgence;
    @Schema(description = "Lignes budgétaires (optionnel)")
    private List<Map<String, Object>> lignes;

    private static String required(Map<String, Object> payload, String key) {
        Object value = payload.get(key);
        if (value == null || value.toString().isBlank()) {
            throw new IllegalArgumentException("Le champ '" + key + "' est obligatoire");
        }
        return value.toString().trim();
    }

    public static CreerBudgetServiceRequestDTO fromMap(java.util.Map<String, Object> payload) {
        CreerBudgetServiceRequestDTO dto = new CreerBudgetServiceRequestDTO();
        dto.setCodeBudget((String) payload.get("codeBudget"));
        dto.setAnnee((String) payload.get("annee"));
        if (payload.get("montantTotal") != null) dto.setMontantTotal(new java.math.BigDecimal(payload.get("montantTotal").toString()));
        dto.setStatut((String) payload.get("statut"));
        dto.setIdAgence((String) payload.get("idAgence"));
        return dto;
    }
}
