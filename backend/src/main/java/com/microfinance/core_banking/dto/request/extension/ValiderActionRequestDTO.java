package com.microfinance.core_banking.dto.request.extension;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ValiderActionRequestDTO {
    private String statut;
    private String commentaireValidation;

    public static ValiderActionRequestDTO fromMap(Map<String, Object> payload) {
        ValiderActionRequestDTO dto = new ValiderActionRequestDTO();
        dto.setStatut(payload.get("statut") == null ? null : payload.get("statut").toString());
        dto.setCommentaireValidation(payload.get("commentaireChecker") == null ? null : payload.get("commentaireChecker").toString());
        return dto;
    }
}
