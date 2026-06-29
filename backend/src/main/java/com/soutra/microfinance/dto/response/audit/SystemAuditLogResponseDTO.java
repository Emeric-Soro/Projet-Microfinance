package com.soutra.microfinance.dto.response.audit;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SystemAuditLogResponseDTO {

    private Long id;
    private String action;
    private String resource;
    private String utilisateur;
    private String adresseIp;
    private LocalDateTime dateAction;
    private String statut;
    private String messageErreur;
    private String methode;
    private String idEntite;
    private String detailsAvant;
    private String detailsApres;
}
