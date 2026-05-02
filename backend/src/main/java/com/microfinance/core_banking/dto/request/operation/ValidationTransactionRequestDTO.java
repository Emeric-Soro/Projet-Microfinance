package com.microfinance.core_banking.dto.request.operation;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requête de validation d'une transaction par un superviseur")
public class ValidationTransactionRequestDTO {

    @NotNull(message = "L'id du superviseur est obligatoire")
    @Schema(description = "Identifiant du superviseur (obligatoire)", example = "1")
    private Long idSuperviseur;

    @Size(max = 500, message = "Le motif ne doit pas depasser 500 caracteres")
    @Schema(description = "Motif de la validation (optionnel, max 500 caractères)", example = "Transaction vérifiée et approuvée")
    private String motif;
}
