package com.microfinance.core_banking.dto.response.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.Map;

@Data
@Schema(description = "Inventaire des fonctionnalités disponibles dans le système")
public class FeatureInventoryResponseDTO {
    @Schema(description = "Map des fonctionnalités avec leur statut", example = "{\"comptabilite\":true,\"credit\":true,\"epargne\":false}")
    private Map<String, Object> features;
}
