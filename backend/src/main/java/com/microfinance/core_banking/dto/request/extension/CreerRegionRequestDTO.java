package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête de création d'une région")
public class CreerRegionRequestDTO {
    @NotBlank(message = "Le code region est obligatoire")
    @Size(max = 20)
    @Schema(description = "Code de la région (obligatoire, max 20 caractères)", example = "REG-DK")
    private String codeRegion;

    @NotBlank(message = "Le nom de la region est obligatoire")
    @Size(max = 100)
    @Schema(description = "Nom de la région (obligatoire, max 100 caractères)", example = "Dakar")
    private String nomRegion;

    @Size(max = 20)
    @Schema(description = "Statut de la région (optionnel, max 20 caractères)", example = "ACTIF")
    private String statut;
}
