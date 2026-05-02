package com.microfinance.core_banking.dto.request.operation;

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
@Schema(description = "Requête de transaction simple (dépôt/retrait)")
public class TransactionSimpleRequestDTO {

    @NotBlank(message = "Le numero de compte est obligatoire")
    @Size(max = 50, message = "Le numero de compte ne doit pas depasser 50 caracteres")
    @Schema(description = "Numéro de compte (obligatoire, max 50 caractères)", example = "SN000012345678901")
    private String numCompte;

    @NotNull(message = "Le montant est obligatoire")
    @Positive(message = "Le montant doit etre strictement positif")
    @Schema(description = "Montant de la transaction (obligatoire, strictement positif)", example = "100000.00")
    private BigDecimal montant;

    @NotNull(message = "L'id guichetier est obligatoire")
    @Schema(description = "Identifiant du guichetier (obligatoire)", example = "1")
    private Long idGuichetier;

    @NotNull(message = "L'id de session de caisse est obligatoire")
    @Schema(description = "Identifiant de la session de caisse (obligatoire)", example = "1")
    private Long idSessionCaisse;
}
