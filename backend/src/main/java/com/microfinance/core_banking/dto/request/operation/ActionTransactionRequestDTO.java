package com.microfinance.core_banking.dto.request.operation;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requête d'action sur une transaction (annulation, correction)")
public class ActionTransactionRequestDTO {

    @Size(max = 500, message = "Le motif ne doit pas depasser 500 caracteres")
    @Schema(description = "Motif de l'action (optionnel, max 500 caractères)", example = "Erreur de saisie du montant")
    private String motif;
}
