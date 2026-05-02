package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête de création d'un paramètre d'agence")
public class CreerParametreAgenceRequestDTO {
    @NotNull(message = "L'id de l'agence est obligatoire")
    @Schema(description = "Identifiant de l'agence (obligatoire)", example = "1")
    private Long idAgence;

    @NotBlank(message = "Le code parametre est obligatoire")
    @Size(max = 50)
    @Schema(description = "Code du paramètre (obligatoire, max 50 caractères)", example = "TAUX_PENALITE")
    private String codeParametre;

    @NotBlank(message = "La valeur du parametre est obligatoire")
    @Schema(description = "Valeur du paramètre (obligatoire)", example = "5")
    private String valeurParametre;

    @Size(max = 20)
    @Schema(description = "Type de valeur (optionnel, max 20 caractères)", example = "NUMERIC")
    private String typeValeur;

    @Size(max = 500)
    @Schema(description = "Description du paramètre (optionnel, max 500 caractères)", example = "Taux de pénalité de retard")
    private String descriptionParametre;

    @Schema(description = "Date d'effet (optionnel)", example = "2026-04-01")
    private LocalDate dateEffet;

    @Schema(description = "Date de fin (optionnel)", example = "2026-12-31")
    private LocalDate dateFin;

    @Schema(description = "Paramètre actif (optionnel)", example = "true")
    private Boolean actif;

    @Schema(description = "Version du paramètre (optionnel)", example = "1")
    private Integer versionParametre;
}
