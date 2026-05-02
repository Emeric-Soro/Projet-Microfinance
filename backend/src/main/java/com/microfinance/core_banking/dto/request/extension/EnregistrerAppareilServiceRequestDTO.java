package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête service d'enregistrement d'un appareil")
public class EnregistrerAppareilServiceRequestDTO {
    @Schema(description = "Identifiant du client (optionnel)", example = "1")
    private String idClient;
    @Schema(description = "Empreinte de l'appareil (optionnel)", example = "EMP-001")
    private String empreinteAppareil;
    @Schema(description = "Plateforme (optionnel)", example = "IOS")
    private String plateforme;
    @Schema(description = "Nom de l'appareil (optionnel)", example = "iPhone de Jean")
    private String nomAppareil;
    @Schema(description = "Appareil autorisé (optionnel)", example = "true")
    private Boolean autorise;

    public static EnregistrerAppareilServiceRequestDTO fromMap(Map<String, Object> payload) {
        EnregistrerAppareilServiceRequestDTO dto = new EnregistrerAppareilServiceRequestDTO();
        dto.setIdClient(required(payload, "idClient"));
        dto.setEmpreinteAppareil(required(payload, "empreinteAppareil"));
        dto.setPlateforme(required(payload, "plateforme"));
        dto.setNomAppareil((String) payload.get("nomAppareil"));
        dto.setAutorise(payload.get("autorise") == null || Boolean.parseBoolean(payload.get("autorise").toString()));
        return dto;
    }

    private static String required(Map<String, Object> payload, String key) {
        Object value = payload.get(key);
        if (value == null || value.toString().isBlank()) {
            throw new IllegalArgumentException("Le champ '" + key + "' est obligatoire");
        }
        return value.toString().trim();
    }

    public static EnregistrerAppareilServiceRequestDTO fromEnregistrerAppareilRequestDTO(EnregistrerAppareilRequestDTO source) {
        EnregistrerAppareilServiceRequestDTO dto = new EnregistrerAppareilServiceRequestDTO();
        dto.setIdClient(String.valueOf(source.getIdAgence()));
        dto.setEmpreinteAppareil(source.getCodeAppareil());
        dto.setPlateforme(source.getTypeAppareil());
        dto.setNomAppareil(source.getLibelle());
        dto.setAutorise("ACTIF".equalsIgnoreCase(source.getStatut()));
        return dto;
    }
}
