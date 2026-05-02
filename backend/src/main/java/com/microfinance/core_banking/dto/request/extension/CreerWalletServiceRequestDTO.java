package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête service de création d'un wallet")
public class CreerWalletServiceRequestDTO {
    @Schema(description = "Identifiant du client (optionnel)", example = "1")
    private String idClient;
    @Schema(description = "Identifiant de l'opérateur mobile money (optionnel)", example = "1")
    private String idOperateurMobileMoney;
    @Schema(description = "Identifiant du compte (optionnel)", example = "1")
    private String idCompte;
    @Schema(description = "Numéro du wallet (optionnel)", example = "771234567")
    private String numeroWallet;
    @Schema(description = "Statut (optionnel)", example = "ACTIF")
    private String statut;

    private static String required(Map<String, Object> payload, String key) {
        Object value = payload.get(key);
        if (value == null || value.toString().isBlank()) {
            throw new IllegalArgumentException("Le champ '" + key + "' est obligatoire");
        }
        return value.toString().trim();
    }

    public static CreerWalletServiceRequestDTO fromMap(java.util.Map<String, Object> payload) {
        CreerWalletServiceRequestDTO dto = new CreerWalletServiceRequestDTO();
        dto.setIdClient((String) payload.get("idClient"));
        dto.setIdOperateurMobileMoney((String) payload.get("idOperateurMobileMoney"));
        dto.setIdCompte((String) payload.get("idCompte"));
        dto.setNumeroWallet((String) payload.get("numeroWallet"));
        dto.setStatut((String) payload.get("statut"));
        return dto;
    }
}
