package com.microfinance.core_banking.service.extension.action;

import com.microfinance.core_banking.dto.request.extension.CreerAlerteServiceRequestDTO;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.service.extension.ConformiteExtensionService;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class CreateAlerteConformiteHandler implements PendingActionHandler {
    private final ConformiteExtensionService conformiteExtensionService;
    public CreateAlerteConformiteHandler(ConformiteExtensionService conformiteExtensionService) {
        this.conformiteExtensionService = conformiteExtensionService;
    }
    @Override
    public String execute(ActionEnAttente action, Map<String, Object> payload) {
        CreerAlerteServiceRequestDTO dto = CreerAlerteServiceRequestDTO.fromMap(payload);
        return String.valueOf(conformiteExtensionService.creerAlerte(dto).getIdAlerteConformite());
    }
    @Override
    public String getTypeAction() { return "CREATE_ALERTE_CONFORMITE"; }
}
