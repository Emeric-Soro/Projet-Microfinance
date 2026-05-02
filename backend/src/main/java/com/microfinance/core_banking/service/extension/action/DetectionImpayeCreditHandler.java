package com.microfinance.core_banking.service.extension.action;

import com.microfinance.core_banking.dto.request.extension.DetecterImpayesRequestDTO;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.service.extension.CreditExtensionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class DetectionImpayeCreditHandler implements PendingActionHandler {
    private final CreditExtensionService creditExtensionService;
    private final ObjectMapper objectMapper;
    public DetectionImpayeCreditHandler(CreditExtensionService creditExtensionService, ObjectMapper objectMapper) {
        this.creditExtensionService = creditExtensionService;
        this.objectMapper = objectMapper;
    }
    @Override
    public String execute(ActionEnAttente action, Map<String, Object> payload) {
        DetecterImpayesRequestDTO dto = objectMapper.convertValue(payload, DetecterImpayesRequestDTO.class);
        return String.valueOf(creditExtensionService.detecterImpayes(dto).size());
    }
    @Override
    public String getTypeAction() { return "DETECTION_IMPAYE_CREDIT"; }
}
