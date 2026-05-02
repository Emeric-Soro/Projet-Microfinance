package com.microfinance.core_banking.service.extension.action;

import com.microfinance.core_banking.dto.request.extension.RescannerClientServiceRequestDTO;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.service.extension.ConformiteExtensionService;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class RescanClientConformiteHandler implements PendingActionHandler {
    private final ConformiteExtensionService conformiteExtensionService;
    public RescanClientConformiteHandler(ConformiteExtensionService conformiteExtensionService) {
        this.conformiteExtensionService = conformiteExtensionService;
    }
    @Override
    public String execute(ActionEnAttente action, Map<String, Object> payload) {
        RescannerClientServiceRequestDTO dto = RescannerClientServiceRequestDTO.fromMap(payload);
        return String.valueOf(conformiteExtensionService.rescannerClient(
                Long.valueOf(action.getReferenceRessource()), dto).getIdAlerteConformite());
    }
    @Override
    public String getTypeAction() { return "RESCAN_CLIENT_CONFORMITE"; }
}
