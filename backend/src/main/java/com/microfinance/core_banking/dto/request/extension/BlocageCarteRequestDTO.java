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
@Schema(description = "Requête de blocage ou déblocage d'une carte bancaire")
public class BlocageCarteRequestDTO {

    @NotBlank(message = "Le motif est obligatoire")
    @Size(max = 200, message = "Le motif ne doit pas dépasser 200 caractères")
    @Schema(description = "Motif du blocage (perte, vol, fraude, etc.)", example = "Perte déclarée par le client")
    private String motif;
}
