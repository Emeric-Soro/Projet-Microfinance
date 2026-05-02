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
@Schema(description = "Requête de révocation d'un consentement Open Banking")
public class RevocationConsentementRequestDTO {

    @NotBlank(message = "Le motif de révocation est obligatoire")
    @Size(max = 200, message = "Le motif ne doit pas dépasser 200 caractères")
    @Schema(description = "Motif de la révocation", example = "Fin de la relation partenariale")
    private String motif;
}
