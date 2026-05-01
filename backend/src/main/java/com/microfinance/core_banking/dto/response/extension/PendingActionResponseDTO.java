package com.microfinance.core_banking.dto.response.extension;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PendingActionResponseDTO {
    private Long idActionEnAttente;
    private String typeAction;
    private String ressource;
    private String referenceRessource;
    private String statut;
    private String commentaireMaker;
    private String commentaireChecker;
    private Long idMaker;
    private Long idChecker;
    private LocalDateTime dateValidation;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
