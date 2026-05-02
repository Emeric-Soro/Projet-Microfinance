package com.microfinance.core_banking.service.extension.action;

import com.microfinance.core_banking.dto.request.extension.CreerStressTestServiceRequestDTO;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.service.extension.RisqueExtensionService;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class CreateStressTestHandler implements PendingActionHandler {
    private final RisqueExtensionService risqueExtensionService;
    public CreateStressTestHandler(RisqueExtensionService risqueExtensionService) {
        this.risqueExtensionService = risqueExtensionService;
    }
    @Override
    public String execute(ActionEnAttente action, Map<String, Object> payload) {
        CreerStressTestServiceRequestDTO dto = CreerStressTestServiceRequestDTO.fromMap(payload);
        return String.valueOf(risqueExtensionService.creerStressTest(dto).getIdStressTest());
    }
    @Override
    public String getTypeAction() { return "CREATE_STRESS_TEST"; }
}
