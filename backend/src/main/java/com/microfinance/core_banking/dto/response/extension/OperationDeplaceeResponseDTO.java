package com.microfinance.core_banking.dto.response.extension;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OperationDeplaceeResponseDTO {
    private Long idOperationDeplacee;
    private Long idTransaction;
    private String referenceTransaction;
    private String agenceOrigine;
    private String agenceOperante;
    private String typeOperation;
    private BigDecimal montant;
    private String devise;
    private String referenceOperation;
    private String statut;
    private LocalDateTime dateOperation;
}
