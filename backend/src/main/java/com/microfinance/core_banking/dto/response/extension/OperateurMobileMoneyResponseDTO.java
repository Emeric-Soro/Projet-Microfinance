package com.microfinance.core_banking.dto.response.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Opérateur de Mobile Money")
public class OperateurMobileMoneyResponseDTO {
    @Schema(description = "Identifiant unique de l'opérateur Mobile Money", example = "1")
    private Long idOperateurMobileMoney;

    @Schema(description = "Code de l'opérateur", example = "ORANGE")
    private String codeOperateur;

    @Schema(description = "Nom de l'opérateur", example = "Orange Money")
    private String nomOperateur;

    @Schema(description = "Statut de l'opérateur", example = "ACTIF")
    private String statut;
}
