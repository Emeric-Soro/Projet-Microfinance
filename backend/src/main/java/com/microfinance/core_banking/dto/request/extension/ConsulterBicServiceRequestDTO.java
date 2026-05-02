package com.microfinance.core_banking.dto.request.extension;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ConsulterBicServiceRequestDTO {
    private String idClient;
    private BigDecimal encoursExterne;
    private String codeRapport;
    private String periode;
    private String statut;
    private String referenceConsentement;
    private String nombreEtablissements;

    public static ConsulterBicServiceRequestDTO fromMap(Map<String, Object> payload) {
        ConsulterBicServiceRequestDTO dto = new ConsulterBicServiceRequestDTO();
        dto.setIdClient(required(payload, "idClient"));
        dto.setEncoursExterne(payload.get("encoursExterne") == null ? BigDecimal.ZERO : new BigDecimal(payload.get("encoursExterne").toString()));
        Object codeVal = payload.get("codeRapport");
        dto.setCodeRapport(codeVal == null || codeVal.toString().isBlank() ? null : codeVal.toString().trim());
        Object periodeVal = payload.get("periode");
        dto.setPeriode(periodeVal == null || periodeVal.toString().isBlank() ? null : periodeVal.toString().trim());
        Object statutVal = payload.get("statut");
        dto.setStatut(statutVal == null || statutVal.toString().isBlank() ? "GENERE" : statutVal.toString().trim());
        Object consentVal = payload.get("referenceConsentement");
        dto.setReferenceConsentement(consentVal == null || consentVal.toString().isBlank() ? "N/A" : consentVal.toString().trim());
        Object nbVal = payload.get("nombreEtablissements");
        dto.setNombreEtablissements(nbVal == null || nbVal.toString().isBlank() ? "0" : nbVal.toString().trim());
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
