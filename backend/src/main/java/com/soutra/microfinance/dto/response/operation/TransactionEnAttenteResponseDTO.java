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
public class TransactionEnAttenteResponseDTO {

    private String referenceUnique;
    private String typeOperation;
    private BigDecimal montant;
    private BigDecimal frais;
    private String compteSource;
    private String compteDestination;
    private String clientNom;
    private String demandeurNom;
    private LocalDateTime dateCreation;
    private String statut;
}
