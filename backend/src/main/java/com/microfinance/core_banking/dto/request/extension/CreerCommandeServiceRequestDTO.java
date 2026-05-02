package com.microfinance.core_banking.dto.request.extension;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreerCommandeServiceRequestDTO {
    private String idFournisseur;
    private String referenceCommande;
    private String objet;
    private BigDecimal montant;
    private String dateCommande;
    private String statut;
    private String idAgence;

    public static CreerCommandeServiceRequestDTO fromMap(Map<String, Object> payload) {
        CreerCommandeServiceRequestDTO dto = new CreerCommandeServiceRequestDTO();
        dto.setIdFournisseur(required(payload, "idFournisseur"));
        Object refVal = payload.get("referenceCommande");
        dto.setReferenceCommande(refVal == null || refVal.toString().isBlank() ? null : refVal.toString().trim());
        dto.setObjet(required(payload, "objet"));
        dto.setMontant(payload.get("montant") == null ? BigDecimal.ZERO : new BigDecimal(payload.get("montant").toString()));
        dto.setDateCommande(payload.get("dateCommande") == null ? null : payload.get("dateCommande").toString());
        Object statutVal = payload.get("statut");
        dto.setStatut(statutVal == null || statutVal.toString().isBlank() ? "INITIEE" : statutVal.toString().trim());
        dto.setIdAgence(payload.get("idAgence") == null ? null : payload.get("idAgence").toString());
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
