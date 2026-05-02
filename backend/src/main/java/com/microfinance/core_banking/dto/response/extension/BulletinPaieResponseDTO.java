package com.microfinance.core_banking.dto.response.extension;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BulletinPaieResponseDTO {
    private Long idBulletinPaie;
    private String employe;
    private String periode;
    private BigDecimal salaireBrut;
    private BigDecimal retenues;
    private BigDecimal salaireNet;
    private String statut;
}
