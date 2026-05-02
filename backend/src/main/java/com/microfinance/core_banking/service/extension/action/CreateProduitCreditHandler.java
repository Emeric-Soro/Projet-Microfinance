package com.microfinance.core_banking.service.extension.action;

import com.microfinance.core_banking.dto.request.extension.CreerProduitCreditRequestDTO;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.service.extension.CreditExtensionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class CreateProduitCreditHandler implements PendingActionHandler {
    private final CreditExtensionService creditExtensionService;
    private final ObjectMapper objectMapper;
    public CreateProduitCreditHandler(CreditExtensionService creditExtensionService, ObjectMapper objectMapper) {
        this.creditExtensionService = creditExtensionService;
        this.objectMapper = objectMapper;
    }
    @Override
    public String execute(ActionEnAttente action, Map<String, Object> payload) {
        CreerProduitCreditRequestDTO dto = objectMapper.convertValue(payload, CreerProduitCreditRequestDTO.class);
        return String.valueOf(creditExtensionService.creerProduit(dto).getIdProduitCredit());
    }
    @Override
    public String getTypeAction() { return "CREATE_PRODUIT_CREDIT"; }
}
