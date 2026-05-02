package com.microfinance.core_banking.dto.request.extension;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class OperationDeplaceeRequestDTO {
    @NotNull(message = "L'id de la transaction est obligatoire")
    private Long idTransaction;

    private Long idAgenceOrigine;

    private Long idAgenceOperante;

    @Size(max = 50)
    private String typeOperation;

    private BigDecimal montant;

    @Size(max = 10)
    private String devise;

    @Size(max = 20)
    private String statut;

    @Size(max = 50)
    private String referenceOperation;

    @Size(max = 500)
    private String commentaire;

    private BigDecimal tauxCommission;

    private BigDecimal montantCommission;

    private Long idCompteComptable;

    @Size(max = 20)
    private String statutCommission;

    @Size(max = 50)
    private String referencePiece;

    private LocalDateTime dateComptabilisation;
}
