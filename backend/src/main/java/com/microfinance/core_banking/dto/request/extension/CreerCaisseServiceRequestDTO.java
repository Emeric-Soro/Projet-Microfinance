package com.microfinance.core_banking.dto.request.extension;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreerCaisseServiceRequestDTO {
    private String idAgence;
    private String codeCaisse;
    private String libelle;
    private String statut;
    private BigDecimal soldeTheorique;
    private String idGuichet;

    public static CreerCaisseServiceRequestDTO fromMap(Map<String, Object> payload) {
        CreerCaisseServiceRequestDTO dto = new CreerCaisseServiceRequestDTO();
        dto.setIdAgence(required(payload, "idAgence"));
        dto.setCodeCaisse(required(payload, "codeCaisse"));
        dto.setLibelle(required(payload, "libelle"));
        Object statutVal = payload.get("statut");
        dto.setStatut(statutVal == null || statutVal.toString().isBlank() ? "ACTIVE" : statutVal.toString().trim());
        dto.setSoldeTheorique(payload.get("soldeTheorique") == null ? BigDecimal.ZERO : new BigDecimal(payload.get("soldeTheorique").toString()));
        dto.setIdGuichet(payload.get("idGuichet") == null ? null : payload.get("idGuichet").toString());
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
