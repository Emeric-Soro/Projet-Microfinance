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
@Schema(description = "Requête de test d'un schéma comptable")
public class TesterSchemaComptableRequestDTO {
    @NotBlank(message = "Le code operation est obligatoire")
    @Size(max = 50)
    @Schema(description = "Code de l'opération (obligatoire, max 50 caractères)", example = "VERS_EPARGNE")
    private String codeOperation;

    @NotNull(message = "Le montant est obligatoire")
    @Positive(message = "Le montant doit etre positif")
    @Schema(description = "Montant (obligatoire, positif)", example = "100000.00")
    private BigDecimal montant;

    @Schema(description = "Frais (optionnel)", example = "1000.00")
    private BigDecimal frais;

    @Size(max = 100)
    @Schema(description = "Référence débit (optionnel, max 100 caractères)", example = "REF-DEBIT-001")
    private String referenceDebit;

    @Size(max = 255)
    @Schema(description = "Libellé débit (optionnel, max 255 caractères)", example = "Versement épargne")
    private String libelleDebit;

    @Size(max = 100)
    @Schema(description = "Référence crédit (optionnel, max 100 caractères)", example = "REF-CREDIT-001")
    private String referenceCredit;

    @Size(max = 255)
    @Schema(description = "Libellé crédit (optionnel, max 255 caractères)", example = "Intérêts crédités")
    private String libelleCredit;

    @Size(max = 100)
    @Schema(description = "Référence frais (optionnel, max 100 caractères)", example = "REF-FRAIS-001")
    private String referenceFrais;

    @Size(max = 255)
    @Schema(description = "Libellé frais (optionnel, max 255 caractères)", example = "Frais de dossier")
    private String libelleFrais;
}
