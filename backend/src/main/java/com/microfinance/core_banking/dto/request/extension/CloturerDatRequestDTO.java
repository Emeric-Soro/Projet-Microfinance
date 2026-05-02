package com.microfinance.core_banking.dto.request.extension;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CloturerDatRequestDTO {
    private Long idUtilisateurOperateur;

    public static CloturerDatRequestDTO fromMap(Map<String, Object> payload, Long defaultUserId) {
        CloturerDatRequestDTO dto = new CloturerDatRequestDTO();
        dto.setIdUtilisateurOperateur(payload.get("idUtilisateurOperateur") == null ? defaultUserId : Long.valueOf(payload.get("idUtilisateurOperateur").toString()));
        return dto;
    }
}
