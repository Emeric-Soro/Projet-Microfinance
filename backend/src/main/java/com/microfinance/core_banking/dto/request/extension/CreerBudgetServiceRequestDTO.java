package com.microfinance.core_banking.dto.request.extension;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreerBudgetServiceRequestDTO {
    private String codeBudget;
    private String annee;
    private BigDecimal montantTotal;
    private String statut;
    private String idAgence;
    private List<Map<String, Object>> lignes;

    @SuppressWarnings("unchecked")
    public static CreerBudgetServiceRequestDTO fromMap(Map<String, Object> payload) {
        CreerBudgetServiceRequestDTO dto = new CreerBudgetServiceRequestDTO();
        Object codeVal = payload.get("codeBudget");
        dto.setCodeBudget(codeVal == null || codeVal.toString().isBlank() ? null : codeVal.toString().trim());
        dto.setAnnee(required(payload, "annee"));
        dto.setMontantTotal(payload.get("montantTotal") == null ? BigDecimal.ZERO : new BigDecimal(payload.get("montantTotal").toString()));
        Object statutVal = payload.get("statut");
        dto.setStatut(statutVal == null || statutVal.toString().isBlank() ? "BROUILLON" : statutVal.toString().trim());
        dto.setIdAgence(payload.get("idAgence") == null ? null : payload.get("idAgence").toString());
        dto.setLignes(payload.get("lignes") == null ? null : (List<Map<String, Object>>) payload.get("lignes"));
        return dto;
    }

    private static String required(Map<String, Object> payload, String key) {
        Object value = payload.get(key);
        if (value == null || value.toString().isBlank()) {
            throw new IllegalArgumentException("Le champ '" + key + "' est obligatoire");
        }
        return value.toString().trim();
    }
}
