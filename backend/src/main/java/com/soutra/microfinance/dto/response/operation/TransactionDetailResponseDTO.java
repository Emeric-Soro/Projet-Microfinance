package com.soutra.microfinance.dto.response.operation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDetailResponseDTO {

    private String referenceUnique;
    private String typeOperation;
    private BigDecimal montant;
    private BigDecimal frais;
    private BigDecimal montantNet;
    private String mode;
    private LocalDateTime dateHeure;
    private String statutOperation;
    private String numCompteSource;
    private String numCompteDestination;
    private String clientNom;
    private String agentNom;
    private String validateurNom;
    private LocalDateTime dateValidation;
    private LocalDateTime dateExecution;
    private String motifRejet;
    private Boolean validationSuperviseurRequise;
}
