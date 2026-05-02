package com.microfinance.core_banking.dto.request.extension;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreerOrdrePaiementServiceRequestDTO {
    private String idCompte;
    private String referenceOrdre;
    private String typeFlux;
    private String sens;
    private BigDecimal montant;
    private BigDecimal frais;
    private String referenceExterne;
    private String destinationDetail;
    private String dateInitiation;
    private String statut;
    private String idLotCompensation;

    public static CreerOrdrePaiementServiceRequestDTO fromMap(Map<String, Object> payload) {
        CreerOrdrePaiementServiceRequestDTO dto = new CreerOrdrePaiementServiceRequestDTO();
        dto.setIdCompte(required(payload, "idCompte"));
        Object refVal = payload.get("referenceOrdre");
        dto.setReferenceOrdre(refVal == null || refVal.toString().isBlank() ? null : refVal.toString().trim());
        dto.setTypeFlux(required(payload, "typeFlux").toUpperCase());
        Object sensVal = payload.get("sens");
        dto.setSens(sensVal == null || sensVal.toString().isBlank() ? "DEBIT_CLIENT" : sensVal.toString().trim().toUpperCase());
        dto.setMontant(new BigDecimal(required(payload, "montant")));
        dto.setFrais(payload.get("frais") == null ? BigDecimal.ZERO : new BigDecimal(payload.get("frais").toString()));
        dto.setReferenceExterne(payload.get("referenceExterne") == null ? null : payload.get("referenceExterne").toString().trim());
        dto.setDestinationDetail(payload.get("destinationDetail") == null ? null : payload.get("destinationDetail").toString().trim());
        dto.setDateInitiation(payload.get("dateInitiation") == null ? null : payload.get("dateInitiation").toString());
        Object statutVal = payload.get("statut");
        dto.setStatut(statutVal == null || statutVal.toString().isBlank() ? "INITIE" : statutVal.toString().trim());
        dto.setIdLotCompensation(payload.get("idLotCompensation") == null ? null : payload.get("idLotCompensation").toString());
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
