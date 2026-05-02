package com.microfinance.core_banking.dto.request.extension;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreerWalletServiceRequestDTO {
    private String idClient;
    private String idOperateurMobileMoney;
    private String idCompte;
    private String numeroWallet;
    private String statut;

    public static CreerWalletServiceRequestDTO fromMap(Map<String, Object> payload) {
        CreerWalletServiceRequestDTO dto = new CreerWalletServiceRequestDTO();
        dto.setIdClient(required(payload, "idClient"));
        dto.setIdOperateurMobileMoney(required(payload, "idOperateurMobileMoney"));
        dto.setIdCompte(required(payload, "idCompte"));
        dto.setNumeroWallet(required(payload, "numeroWallet"));
        Object statutVal = payload.get("statut");
        dto.setStatut(statutVal == null || statutVal.toString().isBlank() ? "ACTIF" : statutVal.toString().trim());
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
