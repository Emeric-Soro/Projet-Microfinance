package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête de clôture d'un Dépôt à Terme (DAT)")
public class CloturerDatRequestDTO {
    @Schema(description = "Identifiant de l'utilisateur opérateur (optionnel)", example = "1")
    private Long idUtilisateurOperateur;

    public static CloturerDatRequestDTO fromMap(java.util.Map<String, Object> payload) {
        CloturerDatRequestDTO dto = new CloturerDatRequestDTO();
        if (payload.get("idUtilisateurOperateur") != null) dto.setIdUtilisateurOperateur(Long.valueOf(payload.get("idUtilisateurOperateur").toString()));
        return dto;
    }
}
