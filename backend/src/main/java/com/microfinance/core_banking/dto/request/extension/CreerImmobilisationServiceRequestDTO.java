package com.microfinance.core_banking.dto.request.extension;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreerImmobilisationServiceRequestDTO {
    private String codeImmobilisation;
    private String libelle;
    private BigDecimal valeurOrigine;
    private String dureeAmortissementMois;
    private BigDecimal valeurNette;
    private String dateAcquisition;
    private String statut;
    private String idAgence;

    public static CreerImmobilisationServiceRequestDTO fromMap(Map<String, Object> payload) {
        CreerImmobilisationServiceRequestDTO dto = new CreerImmobilisationServiceRequestDTO();
        Object codeVal = payload.get("codeImmobilisation");
        dto.setCodeImmobilisation(codeVal == null || codeVal.toString().isBlank() ? null : codeVal.toString().trim());
        dto.setLibelle(required(payload, "libelle"));
        dto.setValeurOrigine(payload.get("valeurOrigine") == null ? BigDecimal.ZERO : new BigDecimal(payload.get("valeurOrigine").toString()));
        dto.setDureeAmortissementMois(required(payload, "dureeAmortissementMois"));
        dto.setValeurNette(payload.get("valeurNette") == null ? null : new BigDecimal(payload.get("valeurNette").toString()));
        dto.setDateAcquisition(payload.get("dateAcquisition") == null ? null : payload.get("dateAcquisition").toString());
        Object statutVal = payload.get("statut");
        dto.setStatut(statutVal == null || statutVal.toString().isBlank() ? "ACTIVE" : statutVal.toString().trim());
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
