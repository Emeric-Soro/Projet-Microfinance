package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requête de clôture manuelle d'un crédit")
public class ClotureCreditRequestDTO {

    @NotBlank(message = "Le commentaire est obligatoire pour la clôture")
    @Size(max = 500, message = "Le commentaire ne doit pas dépasser 500 caractères")
    @Schema(description = "Motif ou commentaire de la clôture", example = "Crédit remboursé intégralement, clôture administrative")
    private String commentaire;
}
