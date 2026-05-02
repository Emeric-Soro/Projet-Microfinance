package com.microfinance.core_banking.dto.response.extension;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class ControlesComptablesResponseDTO {
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private int totalEcritures;
    private int totalLignes;
    private BigDecimal totalDebit;
    private BigDecimal totalCredit;
    private Boolean equilibreGlobal;
    private int ecrituresSansLignes;
    private List<EcritureDesequilibreeDTO> ecrituresDesequilibrees;
}
