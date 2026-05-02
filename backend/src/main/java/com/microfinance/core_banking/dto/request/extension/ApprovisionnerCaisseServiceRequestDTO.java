package com.microfinance.core_banking.dto.request.extension;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ApprovisionnerCaisseServiceRequestDTO {
    private String idCoffre;
    private String idCaisse;
    private BigDecimal montant;
    private String referenceOperation;
    private String commentaire;

    public static ApprovisionnerCaisseServiceRequestDTO fromMap(Map<String, Object> payload) {
        ApprovisionnerCaisseServiceRequestDTO dto = new ApprovisionnerCaisseServiceRequestDTO();
        dto.setIdCoffre(required(payload, "idCoffre"));
        dto.setIdCaisse(required(payload, "idCaisse"));
        dto.setMontant(new BigDecimal(required(payload, "montant")));
        Object refVal = payload.get("referenceOperation");
        dto.setReferenceOperation(refVal == null || refVal.toString().isBlank() ? null : refVal.toString().trim());
        dto.setCommentaire((String) payload.get("commentaire"));
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
