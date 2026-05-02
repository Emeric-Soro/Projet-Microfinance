package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête service de souscription à un DAT")
public class SouscrireDatServiceRequestDTO {
    @Schema(description = "Identifiant du client (optionnel)", example = "1")
    private Long idClient;
    @Schema(description = "Identifiant du produit d'épargne (optionnel)", example = "1")
    private Long idProduitEpargne;
    @Schema(description = "Numéro de compte support (optionnel)", example = "SN000012345678901")
    private String numCompteSupport;
    @Schema(description = "Identifiant de l'utilisateur opérateur (optionnel)", example = "1")
    private Long idUtilisateurOperateur;
    @Schema(description = "Montant (optionnel)", example = "500000.00")
    private String montant;
    @Schema(description = "Durée en mois (optionnel)", example = "12")
    private String dureeMois;
    @Schema(description = "Taux appliqué (optionnel)", example = "5.50")
    private BigDecimal tauxApplique;
    @Schema(description = "Date de souscription (optionnel)", example = "2026-04-01")
    private String dateSouscription;
    @Schema(description = "Renouvellement automatique (optionnel)", example = "true")
    private Boolean renouvellementAuto;
    @Schema(description = "Statut (optionnel)", example = "ACTIF")
    private String statut;

    private static String required(Map<String, Object> payload, String key) {
        Object value = payload.get(key);
        if (value == null || value.toString().isBlank()) {
            throw new IllegalArgumentException("Le champ '" + key + "' est obligatoire");
        }
        return value.toString().trim();
    }

    public static SouscrireDatServiceRequestDTO fromMap(java.util.Map<String, Object> payload) {
        SouscrireDatServiceRequestDTO dto = new SouscrireDatServiceRequestDTO();
        if (payload.get("idClient") != null) dto.setIdClient(Long.valueOf(payload.get("idClient").toString()));
        if (payload.get("idProduitEpargne") != null) dto.setIdProduitEpargne(Long.valueOf(payload.get("idProduitEpargne").toString()));
        dto.setNumCompteSupport((String) payload.get("numCompteSupport"));
        if (payload.get("idUtilisateurOperateur") != null) dto.setIdUtilisateurOperateur(Long.valueOf(payload.get("idUtilisateurOperateur").toString()));
        dto.setMontant((String) payload.get("montant"));
        dto.setDureeMois((String) payload.get("dureeMois"));
        if (payload.get("tauxApplique") != null) dto.setTauxApplique(new java.math.BigDecimal(payload.get("tauxApplique").toString()));
        dto.setDateSouscription((String) payload.get("dateSouscription"));
        if (payload.get("renouvellementAuto") != null) dto.setRenouvellementAuto(Boolean.parseBoolean(payload.get("renouvellementAuto").toString()));
        dto.setStatut((String) payload.get("statut"));
        return dto;
    }
}
