package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête d'opposition sur un chéquier")
public class OpposerChequierRequestDTO {
    @NotBlank(message = "Le motif d'opposition est obligatoire")
    @Schema(description = "Motif de l'opposition (obligatoire)", example = "Vol du chéquier")
    private String motifOpposition;
}
