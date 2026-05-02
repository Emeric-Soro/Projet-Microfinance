package com.microfinance.core_banking.service.extension.action;

import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.service.extension.ConformiteExtensionService;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class RescanTransactionConformiteHandler implements PendingActionHandler {
    private final ConformiteExtensionService conformiteExtensionService;
    public RescanTransactionConformiteHandler(ConformiteExtensionService conformiteExtensionService) {
        this.conformiteExtensionService = conformiteExtensionService;
    }
    @Override
    public String execute(ActionEnAttente action, Map<String, Object> payload) {
        return String.valueOf(conformiteExtensionService.rescannerTransaction(
                Long.valueOf(action.getReferenceRessource())).getIdAlerteConformite());
    }
    @Override
    public String getTypeAction() { return "RESCAN_TRANSACTION_CONFORMITE"; }
}
