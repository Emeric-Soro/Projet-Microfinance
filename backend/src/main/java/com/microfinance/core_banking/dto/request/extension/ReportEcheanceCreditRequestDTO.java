package com.microfinance.core_banking.dto.request.extension;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportEcheanceCreditRequestDTO {

    @NotNull(message = "L'id du credit est obligatoire")
    private Long idCredit;

    @NotNull(message = "L'id de l'echeance est obligatoire")
    private Long idEcheanceCredit;

    @NotNull(message = "La nouvelle date d'echeance est obligatoire")
    private LocalDate nouvelleDateEcheance;

    @Size(max = 500, message = "Le commentaire ne doit pas depasser 500 caracteres")
    private String commentaire;
}
