package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête service de création d'un employé")
public class CreerEmployeServiceRequestDTO {
    @Schema(description = "Matricule de l'employé (optionnel)", example = "EMP-001")
    private String matricule;
    @Schema(description = "Nom complet (optionnel)", example = "Jean Dupont")
    private String nomComplet;
    @Schema(description = "Poste (optionnel)", example = "Agent de crédit")
    private String poste;
    @Schema(description = "Service (optionnel)", example = "Crédit")
    private String service;
    @Schema(description = "Statut (optionnel)", example = "ACTIF")
    private String statut;
    @Schema(description = "Date d'embauche (optionnel)", example = "2026-01-01")
    private String dateEmbauche;
    @Schema(description = "Email (optionnel)", example = "jean.dupont@microfinance.sn")
    private String email;
    @Schema(description = "Téléphone (optionnel)", example = "+221771234567")
    private String telephone;
    @Schema(description = "Identifiant de l'agence (optionnel)", example = "1")
    private String idAgence;

    private static String required(Map<String, Object> payload, String key) {
        Object value = payload.get(key);
        if (value == null || value.toString().isBlank()) {
            throw new IllegalArgumentException("Le champ '" + key + "' est obligatoire");
        }
        return value.toString().trim();
    }

    public static CreerEmployeServiceRequestDTO fromMap(java.util.Map<String, Object> payload) {
        CreerEmployeServiceRequestDTO dto = new CreerEmployeServiceRequestDTO();
        dto.setMatricule((String) payload.get("matricule"));
        dto.setNomComplet((String) payload.get("nomComplet"));
        dto.setPoste((String) payload.get("poste"));
        dto.setService((String) payload.get("service"));
        dto.setStatut((String) payload.get("statut"));
        dto.setDateEmbauche((String) payload.get("dateEmbauche"));
        dto.setEmail((String) payload.get("email"));
        dto.setTelephone((String) payload.get("telephone"));
        dto.setIdAgence((String) payload.get("idAgence"));
        return dto;
    }
}
