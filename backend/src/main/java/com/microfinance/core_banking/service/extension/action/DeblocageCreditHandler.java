package com.microfinance.core_banking.service.extension.action;

import com.microfinance.core_banking.dto.request.extension.DebloquerCreditRequestDTO;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.service.extension.CreditExtensionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class DeblocageCreditHandler implements PendingActionHandler {
    private final CreditExtensionService creditExtensionService;
    private final ObjectMapper objectMapper;
    public DeblocageCreditHandler(CreditExtensionService creditExtensionService, ObjectMapper objectMapper) {
        this.creditExtensionService = creditExtensionService;
        this.objectMapper = objectMapper;
    }
    @Override
    public String execute(ActionEnAttente action, Map<String, Object> payload) {
        DebloquerCreditRequestDTO dto = objectMapper.convertValue(payload, DebloquerCreditRequestDTO.class);
        return String.valueOf(creditExtensionService.debloquerCredit(
                dto.getIdDemande() != null ? dto.getIdDemande() : Long.valueOf(payload.get("idDemande").toString()), dto).getIdCredit());
    }
    @Override
    public String getTypeAction() { return "DEBLOCAGE_CREDIT"; }
}
