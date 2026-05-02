package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête service de re-scannage client")
public class RescannerClientServiceRequestDTO {
    @Schema(description = "Origine du rescannage (optionnel)", example = "RESCAN")
    private String origine;
    @Schema(description = "Sanction hit (optionnel)", example = "false")
    private Boolean sanctionHit;
    @Schema(description = "Niveau de risque (optionnel)", example = "CRITIQUE")
    private String niveauRisque;
    @Schema(description = "Détails (optionnel)", example = "Aucune correspondance trouvée")
    private String details;

    public static RescannerClientServiceRequestDTO fromMap(java.util.Map<String, Object> payload) {
        RescannerClientServiceRequestDTO dto = new RescannerClientServiceRequestDTO();
        dto.setOrigine((String) payload.get("origine"));
        if (payload.get("sanctionHit") != null) dto.setSanctionHit(Boolean.parseBoolean(payload.get("sanctionHit").toString()));
        dto.setNiveauRisque((String) payload.get("niveauRisque"));
        dto.setDetails((String) payload.get("details"));
        return dto;
    }
}
