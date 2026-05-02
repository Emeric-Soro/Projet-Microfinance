package com.microfinance.core_banking.dto.response.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Partenaire API externe")
public class PartenaireApiResponseDTO {
    @Schema(description = "Identifiant unique du partenaire API", example = "1")
    private Long idPartenaireApi;

    @Schema(description = "Code du partenaire", example = "PART-001")
    private String codePartenaire;

    @Schema(description = "Nom du partenaire", example = "Orange Money")
    private String nomPartenaire;

    @Schema(description = "Type de partenaire", example = "MOBILE_MONEY")
    private String typePartenaire;

    @Schema(description = "URL du webhook de notification", example = "https://api.partenaire.sn/webhook")
    private String webhookUrl;

    @Schema(description = "Statut du partenaire", example = "ACTIF")
    private String statut;

    @Schema(description = "Quotas journaliers autorisés", example = "10000")
    private Integer quotasJournaliers;
}
