package com.microfinance.core_banking.service.extension.action;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microfinance.core_banking.dto.request.extension.RembourserAnticipeCreditRequestDTO;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.service.extension.CreditExtensionService;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class RemboursementAnticipeCreditHandler implements PendingActionHandler {

    private final CreditExtensionService creditExtensionService;
    private final ObjectMapper objectMapper;

    public RemboursementAnticipeCreditHandler(CreditExtensionService creditExtensionService, ObjectMapper objectMapper) {
        this.creditExtensionService = creditExtensionService;
        this.objectMapper = objectMapper;
    }

    @Override
    public String execute(ActionEnAttente action, Map<String, Object> payload) {
        RembourserAnticipeCreditRequestDTO dto = objectMapper.convertValue(payload, RembourserAnticipeCreditRequestDTO.class);
        Long idCredit = Long.valueOf(action.getReferenceRessource());
        return creditExtensionService.rembourserAnticipeCredit(idCredit, dto).getReferenceRemboursement();
    }

    @Override
    public String getTypeAction() {
        return "REMBOURSEMENT_ANTICIPE_CREDIT";
    }
}
