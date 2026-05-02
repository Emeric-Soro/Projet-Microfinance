package com.microfinance.core_banking.dto.response.extension;

import lombok.Data;

@Data
public class ActionEnAttenteResponseDTO {
    private Long idActionEnAttente;
    private String typeAction;
    private String ressource;
    private String referenceRessource;
    private String statut;
}
