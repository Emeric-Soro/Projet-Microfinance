package com.microfinance.core_banking.dto.request.compte;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requête de clôture d'un compte")
public class ClotureCompteRequestDTO {

    @NotBlank(message = "Le numero de compte est obligatoire")
    @Size(max = 50, message = "Le numero de compte ne doit pas depasser 50 caracteres")
    @Schema(description = "Numéro du compte à clôturer (obligatoire, max 50 caractères)", example = "SN000012345678901")
    private String numCompte;

    @Size(max = 500, message = "Le motif ne doit pas depasser 500 caracteres")
    @Schema(description = "Motif de la clôture (optionnel, max 500 caractères)", example = "Demande du client")
    private String motif;
}
