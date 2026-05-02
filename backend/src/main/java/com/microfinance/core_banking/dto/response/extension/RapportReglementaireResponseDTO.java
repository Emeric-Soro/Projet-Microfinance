package com.microfinance.core_banking.dto.response.extension;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RapportReglementaireResponseDTO {
    private Long idRapportReglementaire;
    private String codeRapport;
    private String typeRapport;
    private String periode;
    private String statut;
    private String cheminFichier;
    private LocalDateTime dateGeneration;
}
