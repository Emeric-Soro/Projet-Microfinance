package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête de calcul des intérêts sur l'épargne")
public class CalculerInteretsEpargneRequestDTO {
    @Schema(description = "Date de calcul (optionnel)", example = "2026-04-01")
    private String dateCalcul;
    @Schema(description = "Identifiant de l'utilisateur opérateur (optionnel)", example = "1")
    private Long idUtilisateurOperateur;

    public static CalculerInteretsEpargneRequestDTO fromMap(java.util.Map<String, Object> payload) {
        CalculerInteretsEpargneRequestDTO dto = new CalculerInteretsEpargneRequestDTO();
        dto.setDateCalcul((String) payload.get("dateCalcul"));
        if (payload.get("idUtilisateurOperateur") != null) dto.setIdUtilisateurOperateur(Long.valueOf(payload.get("idUtilisateurOperateur").toString()));
        return dto;
    }
}
