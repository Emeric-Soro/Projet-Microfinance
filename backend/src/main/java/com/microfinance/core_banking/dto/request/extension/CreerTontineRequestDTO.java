package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête de création d'une tontine")
public class CreerTontineRequestDTO {
    @NotBlank(message = "Le code tontine est obligatoire")
    @Schema(description = "Code de la tontine (obligatoire)", example = "TON-001")
    private String codeTontine;

    @NotBlank(message = "L'intitule est obligatoire")
    @Schema(description = "Intitulé de la tontine (obligatoire)", example = "Tontine Dakar 2026")
    private String intitule;

    @NotBlank(message = "Le type de tontine est obligatoire")
    @Schema(description = "Type de tontine (obligatoire)", example = "MENSUELLE")
    private String typeTontine;

    @NotNull(message = "Le montant de cotisation est obligatoire")
    @Positive
    @Schema(description = "Montant de la cotisation (obligatoire, positif)", example = "50000.00")
    private BigDecimal montantCotisation;

    @NotBlank(message = "La periodicite est obligatoire")
    @Schema(description = "Périodicité des cotisations (obligatoire)", example = "MENSUEL")
    private String periodicite;

    @NotNull(message = "Le nombre de participants est obligatoire")
    @Positive
    @Schema(description = "Nombre de participants (obligatoire, positif)", example = "10")
    private Integer nombreParticipants;

    @NotNull(message = "La date de debut est obligatoire")
    @Schema(description = "Date de début (obligatoire)", example = "2026-04-01")
    private LocalDate dateDebut;

    @NotNull(message = "L'id de l'agence est obligatoire")
    @Schema(description = "Identifiant de l'agence (obligatoire)", example = "1")
    private Long idAgence;
}
