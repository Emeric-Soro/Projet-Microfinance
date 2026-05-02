package com.microfinance.core_banking.dto.response.extension;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LotCompensationResponseDTO {
    private Long idLotCompensation;
    private String referenceLot;
    private String typeLot;
    private LocalDateTime dateTraitement;
    private String statut;
    private String commentaire;
}
