package com.microfinance.core_banking.service.extension.action;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microfinance.core_banking.dto.request.extension.PassagePerteCreditRequestDTO;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.service.extension.CreditExtensionService;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PassagePerteCreditHandler implements PendingActionHandler {

    private final CreditExtensionService creditExtensionService;
    private final ObjectMapper objectMapper;

    public PassagePerteCreditHandler(CreditExtensionService creditExtensionService, ObjectMapper objectMapper) {
        this.creditExtensionService = creditExtensionService;
        this.objectMapper = objectMapper;
    }

    @Override
    public String execute(ActionEnAttente action, Map<String, Object> payload) {
        PassagePerteCreditRequestDTO dto = objectMapper.convertValue(payload, PassagePerteCreditRequestDTO.class);
        Long idCredit = dto.getIdCredit() != null ? dto.getIdCredit() : Long.valueOf(action.getReferenceRessource());
        return String.valueOf(creditExtensionService.passerEnPerte(idCredit, dto).getIdCredit());
    }

    @Override
    public String getTypeAction() {
        return "PASSAGE_PERTE_CREDIT";
    }
}
