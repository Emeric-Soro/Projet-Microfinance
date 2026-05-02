package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requête de création d'un consentement Open Banking")
public class ConsentementOpenBankingRequestDTO {

    @NotNull(message = "L'identifiant du partenaire est obligatoire")
    @Schema(description = "Identifiant du partenaire API", example = "1")
    private Long idPartenaire;

    @NotNull(message = "L'identifiant du client est obligatoire")
    @Schema(description = "Identifiant du client", example = "5")
    private Long idClient;

    @NotBlank(message = "Le type de consentement est obligatoire")
    @Size(max = 50, message = "Le type de consentement ne doit pas dépasser 50 caractères")
    @Schema(description = "Type de consentement (COMPTES, TRANSACTIONS, etc.)", example = "COMPTES")
    private String typeConsentement;

    @NotBlank(message = "Le scope est obligatoire")
    @Size(max = 200, message = "Le scope ne doit pas dépasser 200 caractères")
    @Schema(description = "Scope d'accès du consentement", example = "accounts.read transactions.read")
    private String scope;
}
