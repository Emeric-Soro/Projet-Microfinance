package com.microfinance.core_banking.dto.request.extension;

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
public class CreerTontineRequestDTO {
    @NotBlank(message = "Le code tontine est obligatoire")
    private String codeTontine;

    @NotBlank(message = "L'intitule est obligatoire")
    private String intitule;

    @NotBlank(message = "Le type de tontine est obligatoire")
    private String typeTontine;

    @NotNull(message = "Le montant de cotisation est obligatoire")
    @Positive
    private BigDecimal montantCotisation;

    @NotBlank(message = "La periodicite est obligatoire")
    private String periodicite;

    @NotNull(message = "Le nombre de participants est obligatoire")
    @Positive
    private Integer nombreParticipants;

    @NotNull(message = "La date de debut est obligatoire")
    private LocalDate dateDebut;

    @NotNull(message = "L'id de l'agence est obligatoire")
    private Long idAgence;
}
