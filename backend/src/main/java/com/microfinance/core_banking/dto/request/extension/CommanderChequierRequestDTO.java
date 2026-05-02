package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête de commande d'un chéquier")
public class CommanderChequierRequestDTO {
    @NotNull(message = "L'id du compte est obligatoire")
    @Schema(description = "Identifiant du compte (obligatoire)", example = "1")
    private Long idCompte;

    @NotNull
    @Positive
    @Schema(description = "Nombre de chèques (obligatoire, positif)", example = "50")
    private Integer nombreCheques;

    @NotBlank(message = "Le premier numero est obligatoire")
    @Schema(description = "Premier numéro du chéquier (obligatoire)", example = "CHQ-001")
    private String premierNumero;
}
