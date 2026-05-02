package com.microfinance.core_banking.service.extension.action;

import com.microfinance.core_banking.dto.request.extension.DeclarerIncidentServiceRequestDTO;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.service.extension.RisqueExtensionService;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class DeclareIncidentOperationnelHandler implements PendingActionHandler {
    private final RisqueExtensionService risqueExtensionService;
    public DeclareIncidentOperationnelHandler(RisqueExtensionService risqueExtensionService) {
        this.risqueExtensionService = risqueExtensionService;
    }
    @Override
    public String execute(ActionEnAttente action, Map<String, Object> payload) {
        DeclarerIncidentServiceRequestDTO dto = DeclarerIncidentServiceRequestDTO.fromMap(payload);
        return String.valueOf(risqueExtensionService.declarerIncident(dto).getIdIncidentOperationnel());
    }
    @Override
    public String getTypeAction() { return "DECLARE_INCIDENT_OPERATIONNEL"; }
}
