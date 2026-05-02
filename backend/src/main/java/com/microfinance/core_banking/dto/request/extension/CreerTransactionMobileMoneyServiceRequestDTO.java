package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête service de création d'une transaction mobile money")
public class CreerTransactionMobileMoneyServiceRequestDTO {
    @Schema(description = "Référence de la transaction (optionnel)", example = "TXN-MM-001")
    private String referenceTransaction;
    @Schema(description = "Identifiant du wallet client (optionnel)", example = "1")
    private String idWalletClient;
    @Schema(description = "Type de transaction (optionnel)", example = "TRANSFERT")
    private String typeTransaction;
    @Schema(description = "Montant (optionnel)", example = "25000.00")
    private BigDecimal montant;
    @Schema(description = "Frais (optionnel)", example = "500.00")
    private BigDecimal frais;
    @Schema(description = "Statut (optionnel)", example = "INITIEE")
    private String statut;

    private static String required(Map<String, Object> payload, String key) {
        Object value = payload.get(key);
        if (value == null || value.toString().isBlank()) {
            throw new IllegalArgumentException("Le champ '" + key + "' est obligatoire");
        }
        return value.toString().trim();
    }

    public static CreerTransactionMobileMoneyServiceRequestDTO fromMap(java.util.Map<String, Object> payload) {
        CreerTransactionMobileMoneyServiceRequestDTO dto = new CreerTransactionMobileMoneyServiceRequestDTO();
        dto.setReferenceTransaction((String) payload.get("referenceTransaction"));
        dto.setIdWalletClient((String) payload.get("idWalletClient"));
        dto.setTypeTransaction((String) payload.get("typeTransaction"));
        if (payload.get("montant") != null) dto.setMontant(new java.math.BigDecimal(payload.get("montant").toString()));
        if (payload.get("frais") != null) dto.setFrais(new java.math.BigDecimal(payload.get("frais").toString()));
        dto.setStatut((String) payload.get("statut"));
        return dto;
    }
}
