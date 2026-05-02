package com.microfinance.core_banking.service.extension.action;

import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.service.extension.RisqueExtensionService;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class ExecuteStressTestHandler implements PendingActionHandler {
    private final RisqueExtensionService risqueExtensionService;
    public ExecuteStressTestHandler(RisqueExtensionService risqueExtensionService) {
        this.risqueExtensionService = risqueExtensionService;
    }
    @Override
    public String execute(ActionEnAttente action, Map<String, Object> payload) {
        return String.valueOf(risqueExtensionService.executerStressTest(
                Long.valueOf(action.getReferenceRessource())).getIdResultatStressTest());
    }
    @Override
    public String getTypeAction() { return "EXECUTE_STRESS_TEST"; }
}
