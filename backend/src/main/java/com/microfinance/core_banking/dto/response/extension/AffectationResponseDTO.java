package com.microfinance.core_banking.dto.response.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "Affectation d'un utilisateur à une agence avec un rôle opératoire")
public class AffectationResponseDTO {
    @Schema(description = "Identifiant unique de l'affectation", example = "1")
    private Long idAffectation;

    @Schema(description = "Identifiant de l'utilisateur affecté", example = "1")
    private Long idUtilisateur;

    @Schema(description = "Nom de l'agence", example = "Agence Dakar Plateau")
    private String nomAgence;

    @Schema(description = "Rôle opératoire dans l'agence", example = "CAISSIER")
    private String roleOperatoire;

    @Schema(description = "Date de début de l'affectation", example = "2026-01-01")
    private LocalDate dateDebut;

    @Schema(description = "Date de fin de l'affectation", example = "2026-12-31")
    private LocalDate dateFin;

    @Schema(description = "Indique si l'affectation est active", example = "true")
    private Boolean actif;
}
