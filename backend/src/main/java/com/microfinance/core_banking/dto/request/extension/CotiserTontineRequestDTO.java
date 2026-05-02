package com.microfinance.core_banking.dto.request.extension;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CotiserTontineRequestDTO {
    @NotNull(message = "L'id du tour est obligatoire")
    private Long idTourTontine;

    @NotNull(message = "L'id du participant est obligatoire")
    private Long idParticipant;

    @NotNull(message = "Le montant cotise est obligatoire")
    @Positive
    private BigDecimal montantCotise;

    @NotNull
    private LocalDate dateCotisation;
}
