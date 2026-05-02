package com.microfinance.core_banking.dto.request.extension;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class EnregistrerAppareilServiceRequestDTO {
    private String idClient;
    private String empreinteAppareil;
    private String plateforme;
    private String nomAppareil;
    private Boolean autorise;

    public static EnregistrerAppareilServiceRequestDTO fromEnregistrerAppareilRequestDTO(EnregistrerAppareilRequestDTO dto) {
        EnregistrerAppareilServiceRequestDTO serviceDto = new EnregistrerAppareilServiceRequestDTO();
        serviceDto.setIdClient(dto.getIdAgence() != null ? dto.getIdAgence().toString() : null);
        serviceDto.setEmpreinteAppareil(dto.getCodeAppareil());
        serviceDto.setPlateforme(dto.getTypeAppareil());
        serviceDto.setNomAppareil(dto.getLibelle());
        serviceDto.setAutorise("ACTIF".equals(dto.getStatut()));
        return serviceDto;
    }

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
}
