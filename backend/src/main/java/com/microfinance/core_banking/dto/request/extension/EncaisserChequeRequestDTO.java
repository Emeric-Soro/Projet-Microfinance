package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête d'encaissement d'un chèque")
public class EncaisserChequeRequestDTO {
    @NotNull(message = "L'id de la remise de cheque est obligatoire")
    @Schema(description = "Identifiant de la remise de chèque (obligatoire)", example = "1")
    private Long idRemiseCheque;
}
