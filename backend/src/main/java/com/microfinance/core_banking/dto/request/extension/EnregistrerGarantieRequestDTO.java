package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête d'enregistrement d'une garantie")
public class EnregistrerGarantieRequestDTO {
    @NotBlank(message = "Le type de garantie est obligatoire")
    @Size(max = 50)
    @Schema(description = "Type de garantie (obligatoire, max 50 caractères)", example = "BIEN_IMMOBILIER")
    private String typeGarantie;

    @NotBlank(message = "La description est obligatoire")
    @Size(max = 500)
    @Schema(description = "Description de la garantie (obligatoire, max 500 caractères)", example = "Maison à usage d'habitation")
    private String description;

    @NotNull(message = "La valeur est obligatoire")
    @Positive
    @Schema(description = "Valeur estimée (obligatoire, positif)", example = "25000000.00")
    private BigDecimal valeur;

    @Schema(description = "Valeur nantie (optionnel)", example = "15000000.00")
    private BigDecimal valeurNantie;

    @Size(max = 20)
    @Schema(description = "Statut (optionnel, max 20 caractères)", example = "ACTIF")
    private String statut;
}
