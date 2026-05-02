package com.microfinance.core_banking.dto.request.extension;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class SouscrireDatServiceRequestDTO {
    private Long idClient;
    private Long idProduitEpargne;
    private String numCompteSupport;
    private Long idUtilisateurOperateur;
    private String montant;
    private String dureeMois;
    private BigDecimal tauxApplique;
    private String dateSouscription;
    private Boolean renouvellementAuto;
    private String statut;

    public static SouscrireDatServiceRequestDTO fromMap(Map<String, Object> payload, Long defaultUserId) {
        SouscrireDatServiceRequestDTO dto = new SouscrireDatServiceRequestDTO();
        dto.setIdClient(Long.valueOf(required(payload, "idClient")));
        dto.setIdProduitEpargne(Long.valueOf(required(payload, "idProduitEpargne")));
        dto.setNumCompteSupport(required(payload, "numCompteSupport"));
        dto.setIdUtilisateurOperateur(payload.get("idUtilisateurOperateur") == null ? defaultUserId : Long.valueOf(payload.get("idUtilisateurOperateur").toString()));
        dto.setMontant(required(payload, "montant"));
        dto.setDureeMois(required(payload, "dureeMois"));
        dto.setTauxApplique(payload.get("tauxApplique") == null ? null : new BigDecimal(payload.get("tauxApplique").toString()));
        dto.setDateSouscription(payload.get("dateSouscription") == null ? null : payload.get("dateSouscription").toString());
        dto.setRenouvellementAuto(Boolean.parseBoolean(String.valueOf(payload.getOrDefault("renouvellementAuto", false))));
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
