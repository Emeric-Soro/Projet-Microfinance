package com.microfinance.core_banking.dto.response.extension;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ClotureComptableResponseDTO {
    private Long idClotureComptable;
    private String typeCloture;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private Integer totalEcritures;
    private String statut;
    private String commentaire;
}
