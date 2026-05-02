package com.microfinance.core_banking.service.extension.action;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microfinance.core_banking.dto.request.extension.RestructurationCreditRequestDTO;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.service.extension.CreditExtensionService;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class RestructurationCreditHandler implements PendingActionHandler {

    private final CreditExtensionService creditExtensionService;
    private final ObjectMapper objectMapper;

    public RestructurationCreditHandler(CreditExtensionService creditExtensionService, ObjectMapper objectMapper) {
        this.creditExtensionService = creditExtensionService;
        this.objectMapper = objectMapper;
    }

    @Override
    public String execute(ActionEnAttente action, Map<String, Object> payload) {
        RestructurationCreditRequestDTO dto = objectMapper.convertValue(payload, RestructurationCreditRequestDTO.class);
        Long idCredit = dto.getIdCredit() != null ? dto.getIdCredit() : Long.valueOf(action.getReferenceRessource());
        return String.valueOf(creditExtensionService.restructurerCredit(idCredit, dto).getIdCredit());
    }

    @Override
    public String getTypeAction() {
        return "RESTRUCTURATION_CREDIT";
    }
}
