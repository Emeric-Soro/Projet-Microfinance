package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête de création d'un compte de liaison inter-agence")
public class CreerCompteLiaisonRequestDTO {
    @NotNull(message = "L'id de l'agence source est obligatoire")
    @Schema(description = "Identifiant de l'agence source (obligatoire)", example = "1")
    private Long idAgenceSource;

    @NotNull(message = "L'id de l'agence destination est obligatoire")
    @Schema(description = "Identifiant de l'agence destination (obligatoire)", example = "2")
    private Long idAgenceDestination;

    @NotNull(message = "L'id du compte comptable est obligatoire")
    @Schema(description = "Identifiant du compte comptable (obligatoire)", example = "1")
    private Long idCompteComptable;

    @Size(max = 255)
    @Schema(description = "Libellé de la liaison (optionnel, max 255 caractères)", example = "Liaison Dakar-Thiès")
    private String libelle;

    @Schema(description = "Liaison active (optionnel)", example = "true")
    private Boolean actif;
}
