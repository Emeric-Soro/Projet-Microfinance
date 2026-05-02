package com.microfinance.core_banking.service.extension.action;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microfinance.core_banking.dto.request.extension.CreerClasseComptableRequestDTO;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.service.extension.ComptabiliteExtensionService;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class CreateClasseComptableHandler implements PendingActionHandler {
    private final ComptabiliteExtensionService comptabiliteExtensionService;
    private final ObjectMapper objectMapper;
    public CreateClasseComptableHandler(ComptabiliteExtensionService comptabiliteExtensionService, ObjectMapper objectMapper) {
        this.comptabiliteExtensionService = comptabiliteExtensionService;
        this.objectMapper = objectMapper;
    }
    @Override
    public String execute(ActionEnAttente action, Map<String, Object> payload) {
        CreerClasseComptableRequestDTO dto = objectMapper.convertValue(payload, CreerClasseComptableRequestDTO.class);
        return String.valueOf(comptabiliteExtensionService.creerClasse(dto).getIdClasseComptable());
    }
    @Override
    public String getTypeAction() { return "CREATE_CLASSE_COMPTABLE"; }
}
