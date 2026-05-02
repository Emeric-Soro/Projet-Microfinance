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
@Schema(description = "Ligne d'écriture comptable")
public class LigneEcritureDTO {
    @NotBlank(message = "Le numero de compte est obligatoire")
    @Size(max = 20)
    @Schema(description = "Numéro de compte comptable (obligatoire, max 20 caractères)", example = "512000")
    private String numeroCompte;

    @NotBlank(message = "Le sens (DEBIT/CREDIT) est obligatoire")
    @Schema(description = "Sens de l'écriture (obligatoire)", example = "DEBIT")
    private String sens;

    @NotNull(message = "Le montant est obligatoire")
    @Positive(message = "Le montant doit etre positif")
    @Schema(description = "Montant de la ligne (obligatoire, positif)", example = "100000.00")
    private BigDecimal montant;

    @Size(max = 100)
    @Schema(description = "Référence auxiliaire (optionnel, max 100 caractères)", example = "AUX-001")
    private String referenceAuxiliaire;

    @Size(max = 255)
    @Schema(description = "Libellé auxiliaire (optionnel, max 255 caractères)", example = "Client Dupont Jean")
    private String libelleAuxiliaire;
}
