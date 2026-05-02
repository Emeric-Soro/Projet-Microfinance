package com.microfinance.core_banking.dto.response.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "Appareil enregistré d'un client")
public class AppareilClientResponseDTO {
    @Schema(description = "Identifiant unique de l'appareil client", example = "1")
    private Long idAppareilClient;

    @Schema(description = "Identifiant du client propriétaire", example = "1")
    private Long idClient;

    @Schema(description = "Empreinte numérique de l'appareil", example = "a1b2c3d4e5f6...")
    private String empreinteAppareil;

    @Schema(description = "Plateforme de l'appareil", example = "Android")
    private String plateforme;

    @Schema(description = "Nom de l'appareil", example = "Samsung Galaxy S24")
    private String nomAppareil;

    @Schema(description = "Indique si l'appareil est autorisé", example = "true")
    private Boolean autorise;

    @Schema(description = "Date et heure de la dernière connexion", example = "2026-04-01T14:30:00")
    private LocalDateTime derniereConnexion;
}
