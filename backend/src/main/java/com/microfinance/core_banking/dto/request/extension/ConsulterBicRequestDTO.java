package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête de consultation BIC (Bureau d'Information sur le Crédit)")
public class ConsulterBicRequestDTO {
    @NotBlank(message = "Le code BIC est obligatoire")
    @Size(max = 20)
    @Schema(description = "Code BIC (obligatoire, max 20 caractères)", example = "BIC-001")
    private String codeBic;

    @Size(max = 50)
    @Schema(description = "Code banque (optionnel, max 50 caractères)", example = "BQ001")
    private String codeBanque;
}
