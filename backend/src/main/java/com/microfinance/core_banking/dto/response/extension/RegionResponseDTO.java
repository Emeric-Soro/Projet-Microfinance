package com.microfinance.core_banking.dto.response.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Région géographique de l'institution")
public class RegionResponseDTO {
    @Schema(description = "Identifiant unique de la région", example = "1")
    private Long idRegion;

    @Schema(description = "Code de la région", example = "REG-DK")
    private String codeRegion;

    @Schema(description = "Nom de la région", example = "Dakar")
    private String nomRegion;

    @Schema(description = "Statut de la région", example = "ACTIF")
    private String statut;
}
