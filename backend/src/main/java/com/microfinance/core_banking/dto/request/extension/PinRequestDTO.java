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
@Schema(description = "Requête de définition du code PIN d'une carte")
public class PinRequestDTO {

    @NotBlank(message = "Le code PIN est obligatoire")
    @Size(min = 4, max = 6, message = "Le code PIN doit contenir entre 4 et 6 caractères")
    @Schema(description = "Code PIN à définir (4 à 6 chiffres)", example = "1234")
    private String pin;
}
