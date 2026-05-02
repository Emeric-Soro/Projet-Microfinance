package com.microfinance.core_banking.dto.response.extension;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class RapprochementResponseDTO {
    private Long idRapprochementInterAgence;
    private String agenceSource;
    private String agenceDestination;
    private LocalDate periodeDebut;
    private LocalDate periodeFin;
    private BigDecimal montantDebit;
    private BigDecimal montantCredit;
    private BigDecimal ecart;
    private String statut;
    private Long idValidateur;
    private LocalDateTime dateRapprochement;
    private String commentaire;
}
