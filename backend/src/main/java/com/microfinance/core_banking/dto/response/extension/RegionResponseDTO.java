package com.microfinance.core_banking.dto.response.extension;

import lombok.Data;

@Data
public class RegionResponseDTO {
    private Long idRegion;
    private String codeRegion;
    private String nomRegion;
    private String statut;
}
