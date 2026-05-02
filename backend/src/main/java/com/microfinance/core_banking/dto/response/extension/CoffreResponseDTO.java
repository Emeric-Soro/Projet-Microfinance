package com.microfinance.core_banking.dto.response.extension;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CoffreResponseDTO {
    private Long idCoffre;
    private String codeCoffre;
    private String libelle;
    private String agence;
    private BigDecimal soldeTheorique;
    private String statut;
}
