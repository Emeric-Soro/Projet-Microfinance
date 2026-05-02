package com.microfinance.core_banking.service.extension.action;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microfinance.core_banking.dto.request.extension.ClotureCreditRequestDTO;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.service.extension.CreditExtensionService;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ClotureCreditHandler implements PendingActionHandler {

    private final CreditExtensionService creditExtensionService;
    private final ObjectMapper objectMapper;

    public ClotureCreditHandler(CreditExtensionService creditExtensionService, ObjectMapper objectMapper) {
        this.creditExtensionService = creditExtensionService;
        this.objectMapper = objectMapper;
    }

    @Override
    public String execute(ActionEnAttente action, Map<String, Object> payload) {
        ClotureCreditRequestDTO dto = objectMapper.convertValue(payload, ClotureCreditRequestDTO.class);
        creditExtensionService.cloturerCredit(action.getReferenceRessource(), dto.getCommentaire());
        return action.getReferenceRessource();
    }

    @Override
    public String getTypeAction() {
        return "CLOTURE_CREDIT";
    }
}
