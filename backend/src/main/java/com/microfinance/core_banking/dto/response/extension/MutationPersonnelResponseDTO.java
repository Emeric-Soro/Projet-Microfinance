package com.microfinance.core_banking.dto.response.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "Mutation de personnel entre agences")
public class MutationPersonnelResponseDTO {
    @Schema(description = "Identifiant unique de la mutation", example = "1")
    private Long idMutationPersonnel;

    @Schema(description = "Identifiant de l'employé muté", example = "1")
    private Long idEmploye;

    @Schema(description = "Agence source", example = "Agence Dakar Plateau")
    private String agenceSource;

    @Schema(description = "Agence destination", example = "Agence Thiès")
    private String agenceDestination;

    @Schema(description = "Date de la mutation", example = "2026-04-01")
    private LocalDate dateMutation;

    @Schema(description = "Motif de la mutation", example = "Renforcement d'équipe")
    private String motif;

    @Schema(description = "Statut de la mutation", example = "APPROUVEE")
    private String statut;

    @Schema(description = "Identifiant du validateur", example = "1")
    private Long idValidateur;

    @Schema(description = "Date de validation", example = "2026-03-30")
    private LocalDate dateValidation;
}
