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
@Schema(description = "Requête de création d'un ordre de compensation")
public class CreerOrdreCompensationRequestDTO {
    @NotBlank(message = "Le code ordre est obligatoire")
    @Size(max = 20)
    @Schema(description = "Code de l'ordre (obligatoire, max 20 caractères)", example = "ORD-001")
    private String codeOrdre;

    @NotNull(message = "L'id lot est obligatoire")
    @Schema(description = "Identifiant du lot (obligatoire)", example = "1")
    private Long idLot;

    @NotNull(message = "L'id transaction est obligatoire")
    @Schema(description = "Identifiant de la transaction (obligatoire)", example = "1")
    private Long idTransaction;

    @NotNull(message = "Le montant est obligatoire")
    @Positive
    @Schema(description = "Montant (obligatoire, positif)", example = "100000.00")
    private BigDecimal montant;

    @NotBlank(message = "Le sens est obligatoire")
    @Size(max = 10)
    @Schema(description = "Sens (obligatoire, max 10 caractères)", example = "DEBIT")
    private String sens;

    @Size(max = 20)
    @Schema(description = "Statut (optionnel, max 20 caractères)", example = "INITIE")
    private String statut;
}
