package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête de clôture d'un Dépôt à Terme (DAT)")
public class CloturerDatRequestDTO {
    @Schema(description = "Identifiant de l'utilisateur opérateur (optionnel)", example = "1")
    private Long idUtilisateurOperateur;

    @Schema(description = "Taux de pénalité pour clôture anticipée en % (optionnel, défaut 3%)", example = "3.0")
    private BigDecimal penaliteTaux;

    @Schema(description = "Indique si la clôture est anticipée (optionnel, défaut false)", example = "false")
    private Boolean clotureAnticipee;

    public static CloturerDatRequestDTO fromMap(Map<String, Object> payload) {
        CloturerDatRequestDTO dto = new CloturerDatRequestDTO();
        if (payload.get("idUtilisateurOperateur") != null) dto.setIdUtilisateurOperateur(Long.valueOf(payload.get("idUtilisateurOperateur").toString()));
        if (payload.get("penaliteTaux") != null) dto.setPenaliteTaux(new BigDecimal(payload.get("penaliteTaux").toString()));
        if (payload.get("clotureAnticipee") != null) dto.setClotureAnticipee(Boolean.valueOf(payload.get("clotureAnticipee").toString()));
        return dto;
    }
}
