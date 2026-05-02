package com.microfinance.core_banking.dto.response.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Schéma comptable définissant les règles de comptabilisation d'une opération")
public class SchemaComptableResponseDTO {
    @Schema(description = "Identifiant unique du schéma comptable", example = "1")
    private Long idSchemaComptable;

    @Schema(description = "Code de l'opération", example = "VERSEMENT_ESPECES")
    private String codeOperation;

    @Schema(description = "Numéro de compte au débit", example = "512000")
    private String compteDebit;

    @Schema(description = "Numéro de compte au crédit", example = "701000")
    private String compteCredit;

    @Schema(description = "Numéro de compte pour les frais", example = "702000")
    private String compteFrais;

    @Schema(description = "Code du journal comptable", example = "CAIS")
    private String journalCode;

    @Schema(description = "Indique si le schéma est actif", example = "true")
    private Boolean actif;
}
