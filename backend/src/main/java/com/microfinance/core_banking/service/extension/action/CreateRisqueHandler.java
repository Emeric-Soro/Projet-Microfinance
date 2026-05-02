package com.microfinance.core_banking.service.extension.action;

import com.microfinance.core_banking.dto.request.extension.CreerRisqueServiceRequestDTO;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.service.extension.RisqueExtensionService;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class CreateRisqueHandler implements PendingActionHandler {
    private final RisqueExtensionService risqueExtensionService;
    public CreateRisqueHandler(RisqueExtensionService risqueExtensionService) {
        this.risqueExtensionService = risqueExtensionService;
    }
    @Override
    public String execute(ActionEnAttente action, Map<String, Object> payload) {
        CreerRisqueServiceRequestDTO dto = CreerRisqueServiceRequestDTO.fromMap(payload);
        return String.valueOf(risqueExtensionService.creerRisque(dto).getIdRisque());
    }
    @Override
    public String getTypeAction() { return "CREATE_RISQUE"; }
}
