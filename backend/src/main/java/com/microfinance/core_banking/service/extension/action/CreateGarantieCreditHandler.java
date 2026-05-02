package com.microfinance.core_banking.service.extension.action;

import com.microfinance.core_banking.dto.request.extension.EnregistrerGarantieRequestDTO;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.service.extension.CreditExtensionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class CreateGarantieCreditHandler implements PendingActionHandler {
    private final CreditExtensionService creditExtensionService;
    private final ObjectMapper objectMapper;
    public CreateGarantieCreditHandler(CreditExtensionService creditExtensionService, ObjectMapper objectMapper) {
        this.creditExtensionService = creditExtensionService;
        this.objectMapper = objectMapper;
    }
    @Override
    public String execute(ActionEnAttente action, Map<String, Object> payload) {
        EnregistrerGarantieRequestDTO dto = objectMapper.convertValue(payload, EnregistrerGarantieRequestDTO.class);
        return String.valueOf(creditExtensionService.enregistrerGarantie(
                Long.valueOf(action.getReferenceRessource()), dto).getIdGarantieCredit());
    }
    @Override
    public String getTypeAction() { return "CREATE_GARANTIE_CREDIT"; }
}
