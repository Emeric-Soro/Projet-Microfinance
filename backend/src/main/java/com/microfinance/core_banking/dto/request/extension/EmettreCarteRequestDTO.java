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

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requête d'émission d'une carte bancaire")
public class EmettreCarteRequestDTO {

    @NotNull(message = "L'identifiant du compte est obligatoire")
    @Schema(description = "Identifiant du compte à lier à la carte", example = "1")
    private Long idCompte;

    @NotBlank(message = "Le type de carte est obligatoire")
    @Size(max = 30, message = "Le type de carte ne doit pas dépasser 30 caractères")
    @Schema(description = "Type de carte (VISA_CLASSIC, VISA_GOLD, VISA_PREMIUM, etc.)", example = "VISA_CLASSIC")
    private String typeCarte;

    @Positive(message = "Le plafond journalier doit être positif")
    @Schema(description = "Plafond journalier de la carte", example = "500000.00")
    private BigDecimal plafondJournalier;

    @Positive(message = "Le plafond mensuel doit être positif")
    @Schema(description = "Plafond mensuel de la carte", example = "5000000.00")
    private BigDecimal plafondMensuel;
}
