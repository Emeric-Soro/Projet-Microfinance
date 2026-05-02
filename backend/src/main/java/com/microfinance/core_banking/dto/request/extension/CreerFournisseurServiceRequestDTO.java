package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête service de création d'un fournisseur")
public class CreerFournisseurServiceRequestDTO {
    @Schema(description = "Code du fournisseur (optionnel)", example = "FOUR-001")
    private String codeFournisseur;
    @Schema(description = "Nom du fournisseur (optionnel)", example = "Fournisseur SA")
    private String nom;
    @Schema(description = "Contact (optionnel)", example = "+221771234567")
    private String contact;
    @Schema(description = "Téléphone (optionnel)", example = "+221338891234")
    private String telephone;
    @Schema(description = "Email (optionnel)", example = "contact@fournisseur.sn")
    private String email;
    @Schema(description = "Statut (optionnel)", example = "ACTIF")
    private String statut;

    private static String required(Map<String, Object> payload, String key) {
        Object value = payload.get(key);
        if (value == null || value.toString().isBlank()) {
            throw new IllegalArgumentException("Le champ '" + key + "' est obligatoire");
        }
        return value.toString().trim();
    }

    public static CreerFournisseurServiceRequestDTO fromMap(java.util.Map<String, Object> payload) {
        CreerFournisseurServiceRequestDTO dto = new CreerFournisseurServiceRequestDTO();
        dto.setCodeFournisseur((String) payload.get("codeFournisseur"));
        dto.setNom((String) payload.get("nom"));
        dto.setContact((String) payload.get("contact"));
        dto.setTelephone((String) payload.get("telephone"));
        dto.setEmail((String) payload.get("email"));
        dto.setStatut((String) payload.get("statut"));
        return dto;
    }
}
