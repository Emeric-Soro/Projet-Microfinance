package com.microfinance.core_banking.dto.request.extension;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreerPartenaireServiceRequestDTO {
    private String codePartenaire;
    private String nomPartenaire;
    private String typePartenaire;
    private String webhookUrl;
    private String statut;
    private String oauthClientId;
    private String cleApi;
    private Integer quotasJournaliers;

    public static CreerPartenaireServiceRequestDTO fromMap(Map<String, Object> payload) {
        CreerPartenaireServiceRequestDTO dto = new CreerPartenaireServiceRequestDTO();
        dto.setCodePartenaire(required(payload, "codePartenaire"));
        dto.setNomPartenaire(required(payload, "nomPartenaire"));
        dto.setTypePartenaire(required(payload, "typePartenaire"));
        dto.setWebhookUrl((String) payload.get("webhookUrl"));
        Object statutVal = payload.get("statut");
        dto.setStatut(statutVal == null || statutVal.toString().isBlank() ? "ACTIF" : statutVal.toString().trim());
        dto.setOauthClientId((String) payload.get("oauthClientId"));
        dto.setCleApi((String) payload.get("cleApi"));
        dto.setQuotasJournaliers(payload.get("quotasJournaliers") == null ? 0 : Integer.parseInt(payload.get("quotasJournaliers").toString()));
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
