package com.microfinance.core_banking.dto.request.extension;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ChangerStatutTransactionMobileMoneyRequestDTO {
    private String statut;

    public static ChangerStatutTransactionMobileMoneyRequestDTO fromMap(Map<String, Object> payload) {
        ChangerStatutTransactionMobileMoneyRequestDTO dto = new ChangerStatutTransactionMobileMoneyRequestDTO();
        dto.setStatut(required(payload, "statut").toUpperCase());
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
