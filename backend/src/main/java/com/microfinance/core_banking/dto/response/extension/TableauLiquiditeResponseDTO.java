package com.microfinance.core_banking.dto.response.extension;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class TableauLiquiditeResponseDTO {
    private List<Map<String, Object>> lignes;
}
