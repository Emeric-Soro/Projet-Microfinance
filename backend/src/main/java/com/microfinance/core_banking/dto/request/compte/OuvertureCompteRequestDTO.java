package com.microfinance.core_banking.dto.request.compte;

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
@Schema(description = "Requête d'ouverture d'un compte client")
public class OuvertureCompteRequestDTO {

    @NotNull(message = "L'id client est obligatoire")
    @Schema(description = "Identifiant du client (obligatoire)", example = "1")
    private Long idClient;

    @NotBlank(message = "Le code type compte est obligatoire")
    @Size(max = 50, message = "Le code type compte ne doit pas depasser 50 caracteres")
    @Schema(description = "Code du type de compte (obligatoire, max 50 caractères)", example = "EPARGNE")
    private String codeTypeCompte;

    @NotNull(message = "Le depot initial est obligatoire")
    @Positive(message = "Le depot initial doit etre strictement positif")
    @Schema(description = "Montant du dépôt initial (obligatoire, strictement positif)", example = "50000.00")
    private BigDecimal depotInitial;
}
