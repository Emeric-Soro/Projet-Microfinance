package com.microfinance.core_banking.service.extension.action;

import com.microfinance.core_banking.dto.request.extension.DeciderDemandeCreditRequestDTO;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.service.extension.CreditExtensionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class DecisionDemandeCreditHandler implements PendingActionHandler {
    private final CreditExtensionService creditExtensionService;
    private final ObjectMapper objectMapper;
    public DecisionDemandeCreditHandler(CreditExtensionService creditExtensionService, ObjectMapper objectMapper) {
        this.creditExtensionService = creditExtensionService;
        this.objectMapper = objectMapper;
    }
    @Override
    public String execute(ActionEnAttente action, Map<String, Object> payload) {
        DeciderDemandeCreditRequestDTO dto = objectMapper.convertValue(payload, DeciderDemandeCreditRequestDTO.class);
        return String.valueOf(creditExtensionService.deciderDemande(
                Long.valueOf(action.getReferenceRessource()), dto).getIdDemandeCredit());
    }
    @Override
    public String getTypeAction() { return "DECISION_DEMANDE_CREDIT"; }
}
