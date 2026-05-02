package com.microfinance.core_banking.dto.response.extension;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AffectationResponseDTO {
    private Long idAffectation;
    private Long idUtilisateur;
    private String nomAgence;
    private String roleOperatoire;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private Boolean actif;
}
