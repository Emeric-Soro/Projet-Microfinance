package com.microfinance.core_banking.dto.response.client;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Informations d'un utilisateur du système")
public class UtilisateurResponseDTO {

    @Schema(description = "Identifiant unique de l'utilisateur", example = "1")
    private Long idUser;

    @Schema(description = "Nom d'utilisateur (login)", example = "jdupont")
    private String login;

    @Schema(description = "Identifiant du client associé", example = "1")
    private Long idClient;

    @Schema(description = "Liste des rôles attribués", example = "[\"ADMIN\",\"AGENT\"]")
    private List<String> roles;

    @Schema(description = "Indique si le compte est actif", example = "true")
    private Boolean actif;

    @Schema(description = "Indique si le compte est verrouillé", example = "true")
    private Boolean compteNonVerrouille;

    @Schema(description = "Date d'expiration des identifiants", example = "2026-12-31T23:59:59")
    private LocalDateTime identifiantsExpirentLe;

    @Schema(description = "Indique si l'authentification à deux facteurs est active", example = "true")
    private Boolean secondFacteurActive;
}
