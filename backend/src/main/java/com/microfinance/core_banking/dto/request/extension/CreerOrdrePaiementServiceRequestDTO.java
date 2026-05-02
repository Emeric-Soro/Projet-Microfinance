package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête service de création d'un ordre de paiement")
public class CreerOrdrePaiementServiceRequestDTO {
    @Schema(description = "Identifiant du compte (optionnel)", example = "1")
    private String idCompte;
    @Schema(description = "Référence de l'ordre (optionnel)", example = "ORD-001")
    private String referenceOrdre;
    @Schema(description = "Type de flux (optionnel)", example = "VIREMENT_SORTANT")
    private String typeFlux;
    @Schema(description = "Sens (optionnel)", example = "DEBIT_CLIENT")
    private String sens;
    @Schema(description = "Montant (optionnel)", example = "100000.00")
    private BigDecimal montant;
    @Schema(description = "Frais (optionnel)", example = "500.00")
    private BigDecimal frais;
    @Schema(description = "Référence externe (optionnel)", example = "EXT-REF-001")
    private String referenceExterne;
    @Schema(description = "Détail de la destination (optionnel)", example = "Compte client 123456")
    private String destinationDetail;
    @Schema(description = "Date d'initiation (optionnel)", example = "2026-04-01")
    private String dateInitiation;
    @Schema(description = "Statut (optionnel)", example = "INITIE")
    private String statut;
    @Schema(description = "Identifiant du lot de compensation (optionnel)", example = "1")
    private String idLotCompensation;

    private static String required(Map<String, Object> payload, String key) {
        Object value = payload.get(key);
        if (value == null || value.toString().isBlank()) {
            throw new IllegalArgumentException("Le champ '" + key + "' est obligatoire");
        }
        return value.toString().trim();
    }

    public static CreerOrdrePaiementServiceRequestDTO fromMap(java.util.Map<String, Object> payload) {
        CreerOrdrePaiementServiceRequestDTO dto = new CreerOrdrePaiementServiceRequestDTO();
        dto.setIdCompte((String) payload.get("idCompte"));
        dto.setReferenceOrdre((String) payload.get("referenceOrdre"));
        dto.setTypeFlux((String) payload.get("typeFlux"));
        dto.setSens((String) payload.get("sens"));
        if (payload.get("montant") != null) dto.setMontant(new java.math.BigDecimal(payload.get("montant").toString()));
        if (payload.get("frais") != null) dto.setFrais(new java.math.BigDecimal(payload.get("frais").toString()));
        dto.setReferenceExterne((String) payload.get("referenceExterne"));
        dto.setDestinationDetail((String) payload.get("destinationDetail"));
        dto.setDateInitiation((String) payload.get("dateInitiation"));
        dto.setStatut((String) payload.get("statut"));
        dto.setIdLotCompensation((String) payload.get("idLotCompensation"));
        return dto;
    }
}
