package com.microfinance.core_banking.service.extension.action;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microfinance.core_banking.dto.request.extension.ClotureComptableRequestDTO;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.service.extension.ComptabiliteExtensionService;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ClotureMensuelleHandler implements PendingActionHandler {

    private final ComptabiliteExtensionService comptabiliteExtensionService;
    private final ObjectMapper objectMapper;

    public ClotureMensuelleHandler(ComptabiliteExtensionService comptabiliteExtensionService, ObjectMapper objectMapper) {
        this.comptabiliteExtensionService = comptabiliteExtensionService;
        this.objectMapper = objectMapper;
    }

    @Override
    public String execute(ActionEnAttente action, Map<String, Object> payload) {
        ClotureComptableRequestDTO dto = objectMapper.convertValue(payload, ClotureComptableRequestDTO.class);
        dto.setTypeCloture("MENSUELLE");
        return String.valueOf(comptabiliteExtensionService.cloturerPeriode(dto).getIdClotureComptable());
    }

    @Override
    public String getTypeAction() {
        return "CLOTURE_MENSUELLE";
    }
}
