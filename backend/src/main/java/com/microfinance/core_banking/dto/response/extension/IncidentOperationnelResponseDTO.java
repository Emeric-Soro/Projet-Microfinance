package com.microfinance.core_banking.dto.response.extension;

import lombok.Data;

@Data
public class IncidentOperationnelResponseDTO {
    private Long idIncidentOperationnel;
    private String referenceIncident;
    private String typeIncident;
    private String gravite;
    private String statut;
    private String description;
    private String risque;
}
