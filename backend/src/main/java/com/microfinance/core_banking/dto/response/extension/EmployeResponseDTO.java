package com.microfinance.core_banking.dto.response.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Informations d'un employé")
public class EmployeResponseDTO {
    @Schema(description = "Identifiant unique de l'employé", example = "1")
    private Long idEmploye;

    @Schema(description = "Matricule de l'employé", example = "EMP-001")
    private String matricule;

    @Schema(description = "Nom complet de l'employé", example = "M. Fall")
    private String nomComplet;

    @Schema(description = "Poste occupé", example = "Agent de crédit")
    private String poste;

    @Schema(description = "Service rattaché", example = "Crédit")
    private String service;

    @Schema(description = "Statut de l'employé", example = "ACTIF")
    private String statut;

    @Schema(description = "Agence de rattachement", example = "Agence Dakar Plateau")
    private String agence;
}
