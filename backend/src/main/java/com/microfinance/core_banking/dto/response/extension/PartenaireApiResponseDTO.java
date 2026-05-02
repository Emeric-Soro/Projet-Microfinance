package com.microfinance.core_banking.dto.response.extension;

import lombok.Data;

@Data
public class PartenaireApiResponseDTO {
    private Long idPartenaireApi;
    private String codePartenaire;
    private String nomPartenaire;
    private String typePartenaire;
    private String webhookUrl;
    private String statut;
    private Integer quotasJournaliers;
}
