package com.microfinance.core_banking.service.extension.action;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microfinance.core_banking.dto.request.extension.CreerEcritureManuelleRequestDTO;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.service.extension.ComptabiliteExtensionService;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class CreateEcritureManuelleHandler implements PendingActionHandler {
    private final ComptabiliteExtensionService comptabiliteExtensionService;
    private final ObjectMapper objectMapper;
    public CreateEcritureManuelleHandler(ComptabiliteExtensionService comptabiliteExtensionService, ObjectMapper objectMapper) {
        this.comptabiliteExtensionService = comptabiliteExtensionService;
        this.objectMapper = objectMapper;
    }
    @Override
    public String execute(ActionEnAttente action, Map<String, Object> payload) {
        CreerEcritureManuelleRequestDTO dto = objectMapper.convertValue(payload, CreerEcritureManuelleRequestDTO.class);
        return String.valueOf(comptabiliteExtensionService.creerEcritureManuelle(dto).getIdEcritureComptable());
    }
    @Override
    public String getTypeAction() { return "CREATE_ECRITURE_MANUELLE"; }
}
