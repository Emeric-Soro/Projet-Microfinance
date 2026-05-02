package com.microfinance.core_banking.dto.response.extension;

import lombok.Data;

@Data
public class GuichetResponseDTO {
    private Long idGuichet;
    private String codeGuichet;
    private String nomGuichet;
    private String statut;
    private String nomAgence;
}
