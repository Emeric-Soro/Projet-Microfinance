package com.microfinance.core_banking.dto.response.extension;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CaisseResponseDTO {
    private Long idCaisse;
    private String codeCaisse;
    private String libelle;
    private String agence;
    private String guichet;
    private String statut;
    private BigDecimal soldeTheorique;
}
