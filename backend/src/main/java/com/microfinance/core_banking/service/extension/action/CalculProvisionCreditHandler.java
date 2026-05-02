package com.microfinance.core_banking.service.extension.action;

import com.microfinance.core_banking.dto.request.extension.CalculerProvisionsRequestDTO;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.service.extension.CreditExtensionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class CalculProvisionCreditHandler implements PendingActionHandler {
    private final CreditExtensionService creditExtensionService;
    private final ObjectMapper objectMapper;
    public CalculProvisionCreditHandler(CreditExtensionService creditExtensionService, ObjectMapper objectMapper) {
        this.creditExtensionService = creditExtensionService;
        this.objectMapper = objectMapper;
    }
    @Override
    public String execute(ActionEnAttente action, Map<String, Object> payload) {
        CalculerProvisionsRequestDTO dto = objectMapper.convertValue(payload, CalculerProvisionsRequestDTO.class);
        return String.valueOf(creditExtensionService.calculerProvisions(dto).size());
    }
    @Override
    public String getTypeAction() { return "CALCUL_PROVISION_CREDIT"; }
}
