package com.microfinance.core_banking.dto.request.extension;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreerTransactionMobileMoneyServiceRequestDTO {
    private String referenceTransaction;
    private String idWalletClient;
    private String typeTransaction;
    private BigDecimal montant;
    private BigDecimal frais;
    private String statut;

    public static CreerTransactionMobileMoneyServiceRequestDTO fromMap(Map<String, Object> payload) {
        CreerTransactionMobileMoneyServiceRequestDTO dto = new CreerTransactionMobileMoneyServiceRequestDTO();
        Object refVal = payload.get("referenceTransaction");
        dto.setReferenceTransaction(refVal == null || refVal.toString().isBlank() ? null : refVal.toString().trim());
        dto.setIdWalletClient(required(payload, "idWalletClient"));
        dto.setTypeTransaction(required(payload, "typeTransaction").toUpperCase());
        dto.setMontant(new BigDecimal(required(payload, "montant")));
        dto.setFrais(payload.get("frais") == null ? BigDecimal.ZERO : new BigDecimal(payload.get("frais").toString()));
        Object statutVal = payload.get("statut");
        dto.setStatut(statutVal == null || statutVal.toString().isBlank() ? "INITIEE" : statutVal.toString().trim().toUpperCase());
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
