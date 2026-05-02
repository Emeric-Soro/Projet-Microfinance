package com.microfinance.core_banking.service.extension.action;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microfinance.core_banking.dto.request.extension.CreerCompteComptableRequestDTO;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.service.extension.ComptabiliteExtensionService;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class CreateCompteComptableHandler implements PendingActionHandler {
    private final ComptabiliteExtensionService comptabiliteExtensionService;
    private final ObjectMapper objectMapper;
    public CreateCompteComptableHandler(ComptabiliteExtensionService comptabiliteExtensionService, ObjectMapper objectMapper) {
        this.comptabiliteExtensionService = comptabiliteExtensionService;
        this.objectMapper = objectMapper;
    }
    @Override
    public String execute(ActionEnAttente action, Map<String, Object> payload) {
        CreerCompteComptableRequestDTO dto = objectMapper.convertValue(payload, CreerCompteComptableRequestDTO.class);
        return String.valueOf(comptabiliteExtensionService.creerCompte(dto).getIdCompteComptable());
    }
    @Override
    public String getTypeAction() { return "CREATE_COMPTE_COMPTABLE"; }
}
