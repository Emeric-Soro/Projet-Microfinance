package com.microfinance.core_banking.dto.request.extension;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CalculerInteretsEpargneRequestDTO {
    private String dateCalcul;
    private Long idUtilisateurOperateur;

    public static CalculerInteretsEpargneRequestDTO fromMap(Map<String, Object> payload, Long defaultUserId) {
        CalculerInteretsEpargneRequestDTO dto = new CalculerInteretsEpargneRequestDTO();
        dto.setDateCalcul(payload.get("dateCalcul") == null ? null : payload.get("dateCalcul").toString());
        dto.setIdUtilisateurOperateur(payload.get("idUtilisateurOperateur") == null ? defaultUserId : Long.valueOf(payload.get("idUtilisateurOperateur").toString()));
        return dto;
    }
}
