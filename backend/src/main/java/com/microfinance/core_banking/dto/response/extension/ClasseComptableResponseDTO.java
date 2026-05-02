package com.microfinance.core_banking.dto.response.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Classe comptable du plan comptable")
public class ClasseComptableResponseDTO {
    @Schema(description = "Identifiant unique de la classe comptable", example = "1")
    private Long idClasseComptable;

    @Schema(description = "Code de la classe", example = "5")
    private String codeClasse;

    @Schema(description = "Libellé de la classe", example = "Comptes de trésorerie")
    private String libelle;

    @Schema(description = "Ordre d'affichage dans le plan comptable", example = "5")
    private Integer ordreAffichage;
}
