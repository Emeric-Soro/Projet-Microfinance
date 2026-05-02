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
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête de remboursement de crédit")
public class RembourserCreditRequestDTO {
    @NotNull(message = "Le montant est obligatoire")
    @Positive
    @Schema(description = "Montant du remboursement (obligatoire, positif)", example = "150000.00")
    private BigDecimal montant;

    @NotBlank(message = "Le numero de compte source est obligatoire")
    @Size(max = 30)
    @Schema(description = "Numéro du compte source pour le prélèvement (obligatoire, max 30 caractères)", example = "SN000012345678901")
    private String numCompteSource;

    @Schema(description = "Identifiant de l'utilisateur opérateur (optionnel)", example = "1")
    private Long idUtilisateurOperateur;

    @Size(max = 50)
    @Schema(description = "Référence de la transaction (optionnel, max 50 caractères)", example = "TXN-001")
    private String referenceTransaction;

    @Size(max = 50)
    @Schema(description = "Référence de remboursement (optionnel, max 50 caractères)", example = "REM-001")
    private String referenceRemboursement;

    @Schema(description = "Date de paiement (optionnel)", example = "2026-04-01")
    private LocalDate datePaiement;
}
