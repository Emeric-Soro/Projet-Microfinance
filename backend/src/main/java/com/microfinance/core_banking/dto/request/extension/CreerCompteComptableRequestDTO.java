package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête de création d'un compte comptable")
public class CreerCompteComptableRequestDTO {
    @NotBlank(message = "Le code classe est obligatoire")
    @Size(max = 10)
    @Schema(description = "Code de la classe comptable (obligatoire, max 10 caractères)", example = "5")
    private String codeClasse;

    @NotBlank(message = "Le numero de compte est obligatoire")
    @Size(max = 20)
    @Schema(description = "Numéro de compte comptable (obligatoire, max 20 caractères)", example = "512000")
    private String numeroCompte;

    @NotBlank(message = "L'intitule est obligatoire")
    @Size(max = 200)
    @Schema(description = "Intitulé du compte (obligatoire, max 200 caractères)", example = "Banque")
    private String intitule;

    @Size(max = 10)
    @Schema(description = "Type de solde (optionnel, max 10 caractères)", example = "DEBITEUR")
    private String typeSolde;

    @Schema(description = "Compte interne (optionnel)", example = "false")
    private Boolean compteInterne;

    @Schema(description = "Identifiant de l'agence (optionnel)", example = "1")
    private Long idAgence;
}
