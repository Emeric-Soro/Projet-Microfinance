package com.microfinance.core_banking.service.extension;

import com.microfinance.core_banking.dto.response.extension.FeatureInventoryResponseDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class FeatureInventoryServiceTest {

    @InjectMocks
    private FeatureInventoryService featureInventoryService;

    @Test
    void buildInventory_shouldReturnNonEmptyResponse() {
        FeatureInventoryResponseDTO response = featureInventoryService.buildInventory();
        assertNotNull(response);
        assertNotNull(response.getFeatures());
        assertFalse(response.getFeatures().isEmpty());
        assertTrue(response.getFeatures().containsKey("fonctionnalitesExistantes"));
        assertTrue(response.getFeatures().containsKey("fonctionnalitesNouvelles"));
    }
}
