package com.microfinance.core_banking.service.extension.action;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microfinance.core_banking.dto.request.extension.CreerSchemaComptableRequestDTO;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.service.extension.ComptabiliteExtensionService;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class CreateSchemaComptableHandler implements PendingActionHandler {
    private final ComptabiliteExtensionService comptabiliteExtensionService;
    private final ObjectMapper objectMapper;
    public CreateSchemaComptableHandler(ComptabiliteExtensionService comptabiliteExtensionService, ObjectMapper objectMapper) {
        this.comptabiliteExtensionService = comptabiliteExtensionService;
        this.objectMapper = objectMapper;
    }
    @Override
    public String execute(ActionEnAttente action, Map<String, Object> payload) {
        CreerSchemaComptableRequestDTO dto = objectMapper.convertValue(payload, CreerSchemaComptableRequestDTO.class);
        return String.valueOf(comptabiliteExtensionService.creerSchema(dto).getIdSchemaComptable());
    }
    @Override
    public String getTypeAction() { return "CREATE_SCHEMA_COMPTABLE"; }
}
