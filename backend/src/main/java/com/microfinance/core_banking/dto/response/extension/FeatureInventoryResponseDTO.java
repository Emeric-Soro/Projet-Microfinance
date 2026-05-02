package com.microfinance.core_banking.dto.response.extension;

import lombok.Data;
import java.util.Map;

@Data
public class FeatureInventoryResponseDTO {
    private Map<String, Object> features;
}
