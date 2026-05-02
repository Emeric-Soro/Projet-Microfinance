package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête de changement de statut d'une transaction mobile money")
public class ChangerStatutTransactionMobileMoneyRequestDTO {
    @Schema(description = "Statut (optionnel)", example = "EXECUTEE")
    private String statut;

    private static String required(Map<String, Object> payload, String key) {
        Object value = payload.get(key);
        if (value == null || value.toString().isBlank()) {
            throw new IllegalArgumentException("Le champ '" + key + "' est obligatoire");
        }
        return value.toString().trim();
    }

    public static ChangerStatutTransactionMobileMoneyRequestDTO fromMap(java.util.Map<String, Object> payload) {
        ChangerStatutTransactionMobileMoneyRequestDTO dto = new ChangerStatutTransactionMobileMoneyRequestDTO();
        dto.setStatut((String) payload.get("statut"));
        return dto;
    }
}
