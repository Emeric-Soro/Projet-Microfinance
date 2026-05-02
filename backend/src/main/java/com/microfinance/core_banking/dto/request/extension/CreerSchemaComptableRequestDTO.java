package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête de création d'un schéma comptable")
public class CreerSchemaComptableRequestDTO {
    @NotBlank(message = "Le code operation est obligatoire")
    @Size(max = 50)
    @Schema(description = "Code de l'opération (obligatoire, max 50 caractères)", example = "VERS_EPARGNE")
    private String codeOperation;

    @NotBlank(message = "Le compte debit est obligatoire")
    @Size(max = 20)
    @Schema(description = "Compte débit (obligatoire, max 20 caractères)", example = "512000")
    private String compteDebit;

    @NotBlank(message = "Le compte credit est obligatoire")
    @Size(max = 20)
    @Schema(description = "Compte crédit (obligatoire, max 20 caractères)", example = "771000")
    private String compteCredit;

    @Size(max = 20)
    @Schema(description = "Compte frais (optionnel, max 20 caractères)", example = "671000")
    private String compteFrais;

    @Size(max = 10)
    @Schema(description = "Code journal (optionnel, max 10 caractères)", example = "JRN-01")
    private String journalCode;

    @Schema(description = "Schéma actif (optionnel)", example = "true")
    private Boolean actif;
}
