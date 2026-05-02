package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête d'enregistrement de billetage pour une caisse")
public class BilletageCaisseRequestDTO {
    @NotNull(message = "L'identifiant de la session caisse est obligatoire")
    @Schema(description = "Identifiant de la session caisse", example = "1")
    private Long idSessionCaisse;

    @NotNull(message = "La coupure est obligatoire")
    @Positive
    @Schema(description = "Valeur de la coupure (billet/pièce)", example = "10000")
    private BigDecimal coupure;

    @NotNull(message = "La quantité est obligatoire")
    @Positive
    @Schema(description = "Quantité de billets/pièces", example = "50")
    private Integer quantite;

    @Schema(description = "Type de billetage: BILLET ou PIECE (optionnel, défaut BILLET)", example = "BILLET")
    private String typeBilletage;
}
