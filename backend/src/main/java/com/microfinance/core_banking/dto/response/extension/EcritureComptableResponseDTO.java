package com.microfinance.core_banking.dto.response.extension;

import lombok.Data;

import java.time.LocalDate;

@Data
public class EcritureComptableResponseDTO {
    private Long idEcritureComptable;
    private String referencePiece;
    private String journal;
    private LocalDate dateComptable;
    private LocalDate dateValeur;
    private String libelle;
    private String sourceType;
    private String sourceReference;
    private String statut;
}
