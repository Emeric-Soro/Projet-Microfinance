package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête service de création d'un partenaire")
public class CreerPartenaireServiceRequestDTO {
    @Schema(description = "Code du partenaire (optionnel)", example = "PART-001")
    private String codePartenaire;
    @Schema(description = "Nom du partenaire (optionnel)", example = "Partenaire SA")
    private String nomPartenaire;
    @Schema(description = "Type de partenaire (optionnel)", example = "API")
    private String typePartenaire;
    @Schema(description = "URL du webhook (optionnel)", example = "https://partenaire.sn/webhook")
    private String webhookUrl;
    @Schema(description = "Statut (optionnel)", example = "ACTIF")
    private String statut;
    @Schema(description = "OAuth Client ID (optionnel)", example = "client-123")
    private String oauthClientId;
    @Schema(description = "Clé API (optionnel)", example = "sk-abc123")
    private String cleApi;
    @Schema(description = "Quotas journaliers (optionnel)", example = "1000")
    private Integer quotasJournaliers;

    private static String required(Map<String, Object> payload, String key) {
        Object value = payload.get(key);
        if (value == null || value.toString().isBlank()) {
            throw new IllegalArgumentException("Le champ '" + key + "' est obligatoire");
        }
        return value.toString().trim();
    }

    public static CreerPartenaireServiceRequestDTO fromMap(java.util.Map<String, Object> payload) {
        CreerPartenaireServiceRequestDTO dto = new CreerPartenaireServiceRequestDTO();
        dto.setCodePartenaire((String) payload.get("codePartenaire"));
        dto.setNomPartenaire((String) payload.get("nomPartenaire"));
        dto.setTypePartenaire((String) payload.get("typePartenaire"));
        dto.setWebhookUrl((String) payload.get("webhookUrl"));
        dto.setStatut((String) payload.get("statut"));
        dto.setOauthClientId((String) payload.get("oauthClientId"));
        dto.setCleApi((String) payload.get("cleApi"));
        if (payload.get("quotasJournaliers") != null) dto.setQuotasJournaliers(Integer.valueOf(payload.get("quotasJournaliers").toString()));
        return dto;
    }
}
