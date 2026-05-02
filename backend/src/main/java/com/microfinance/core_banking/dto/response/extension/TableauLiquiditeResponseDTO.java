package com.microfinance.core_banking.dto.response.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
@Schema(description = "Tableau de liquidité comportant les lignes de reporting financier")
public class TableauLiquiditeResponseDTO {
    @Schema(description = "Lignes du tableau de liquidité", example = "[{\"compte\":\"512000\",\"solde\":150000.00}]")
    private List<Map<String, Object>> lignes;
}
