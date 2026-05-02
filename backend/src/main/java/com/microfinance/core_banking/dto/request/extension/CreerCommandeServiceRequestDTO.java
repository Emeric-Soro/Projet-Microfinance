package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête service de création d'une commande")
public class CreerCommandeServiceRequestDTO {
    @Schema(description = "Identifiant du fournisseur (optionnel)", example = "1")
    private String idFournisseur;
    @Schema(description = "Référence de la commande (optionnel)", example = "CMD-001")
    private String referenceCommande;
    @Schema(description = "Objet de la commande (optionnel)", example = "Fournitures de bureau")
    private String objet;
    @Schema(description = "Montant (optionnel)", example = "1500000.00")
    private BigDecimal montant;
    @Schema(description = "Date de la commande (optionnel)", example = "2026-04-01")
    private String dateCommande;
    @Schema(description = "Statut (optionnel)", example = "INITIEE")
    private String statut;
    @Schema(description = "Identifiant de l'agence (optionnel)", example = "1")
    private String idAgence;

    private static String required(Map<String, Object> payload, String key) {
        Object value = payload.get(key);
        if (value == null || value.toString().isBlank()) {
            throw new IllegalArgumentException("Le champ '" + key + "' est obligatoire");
        }
        return value.toString().trim();
    }

    public static CreerCommandeServiceRequestDTO fromMap(java.util.Map<String, Object> payload) {
        CreerCommandeServiceRequestDTO dto = new CreerCommandeServiceRequestDTO();
        dto.setIdFournisseur((String) payload.get("idFournisseur"));
        dto.setReferenceCommande((String) payload.get("referenceCommande"));
        dto.setObjet((String) payload.get("objet"));
        if (payload.get("montant") != null) dto.setMontant(new java.math.BigDecimal(payload.get("montant").toString()));
        dto.setDateCommande((String) payload.get("dateCommande"));
        dto.setStatut((String) payload.get("statut"));
        dto.setIdAgence((String) payload.get("idAgence"));
        return dto;
    }
}
