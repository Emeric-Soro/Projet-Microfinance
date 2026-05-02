package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête de création d'une mutation d'employé")
public class CreerMutationRequestDTO {
    @NotNull(message = "L'id de l'employe est obligatoire")
    @Schema(description = "Identifiant de l'employé (obligatoire)", example = "1")
    private Long idEmploye;

    @Schema(description = "Identifiant de l'agence source (optionnel)", example = "1")
    private Long idAgenceSource;

    @NotNull(message = "L'id de l'agence destination est obligatoire")
    @Schema(description = "Identifiant de l'agence destination (obligatoire)", example = "2")
    private Long idAgenceDestination;

    @Schema(description = "Date de la mutation (optionnel)", example = "2026-04-01")
    private LocalDate dateMutation;

    @Size(max = 500)
    @Schema(description = "Motif de la mutation (optionnel, max 500 caractères)", example = "Demande de l'employé")
    private String motif;

    @Size(max = 20)
    @Schema(description = "Statut (optionnel, max 20 caractères)", example = "INITIEE")
    private String statut;
}
