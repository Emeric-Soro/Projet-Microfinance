package com.microfinance.core_banking.dto.response.extension;

import lombok.Data;

import java.time.LocalDate;

@Data
public class MutationPersonnelResponseDTO {
    private Long idMutationPersonnel;
    private Long idEmploye;
    private String agenceSource;
    private String agenceDestination;
    private LocalDate dateMutation;
    private String motif;
    private String statut;
    private Long idValidateur;
    private LocalDate dateValidation;
}
