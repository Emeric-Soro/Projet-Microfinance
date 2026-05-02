package com.microfinance.core_banking.dto.response.extension;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AlerteConformiteResponseDTO {
    private Long idAlerteConformite;
    private String referenceAlerte;
    private String typeAlerte;
    private String niveauRisque;
    private String statut;
    private String resume;
    private LocalDateTime dateDetection;
}
