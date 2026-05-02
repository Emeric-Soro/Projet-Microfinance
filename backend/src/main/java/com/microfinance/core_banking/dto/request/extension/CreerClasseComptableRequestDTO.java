package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête de création d'une classe comptable")
public class CreerClasseComptableRequestDTO {
    @NotBlank(message = "Le code classe est obligatoire")
    @Size(max = 10)
    @Schema(description = "Code de la classe comptable (obligatoire, max 10 caractères)", example = "1")
    private String codeClasse;

    @NotBlank(message = "Le libelle est obligatoire")
    @Size(max = 100)
    @Schema(description = "Libellé de la classe (obligatoire, max 100 caractères)", example = "Classe 1 - Comptes de capitaux")
    private String libelle;

    @Schema(description = "Ordre d'affichage (optionnel)", example = "1")
    private Integer ordreAffichage;
}
