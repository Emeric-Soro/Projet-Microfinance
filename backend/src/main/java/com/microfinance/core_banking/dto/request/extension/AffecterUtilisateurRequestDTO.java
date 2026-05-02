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
@Schema(description = "Requête d'affectation d'un utilisateur à une agence")
public class AffecterUtilisateurRequestDTO {
    @NotNull(message = "L'id de l'utilisateur est obligatoire")
    @Schema(description = "Identifiant de l'utilisateur (obligatoire)", example = "1")
    private Long idUtilisateur;

    @NotNull(message = "L'id de l'agence est obligatoire")
    @Schema(description = "Identifiant de l'agence (obligatoire)", example = "1")
    private Long idAgence;

    @Size(max = 50)
    @Schema(description = "Rôle opératoire (optionnel, max 50 caractères)", example = "GUICHETIER")
    private String roleOperatoire;

    @Schema(description = "Date de début d'affectation (optionnel)", example = "2026-04-01")
    private LocalDate dateDebut;

    @Schema(description = "Date de fin d'affectation (optionnel)", example = "2027-04-01")
    private LocalDate dateFin;

    @Schema(description = "Affectation active (optionnel)", example = "true")
    private Boolean actif;
}
