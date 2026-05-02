package com.microfinance.core_banking.service.extension.action;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microfinance.core_banking.dto.request.compte.ChangementDecouvertRequestDTO;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.service.compte.CompteService;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ModifyDecouvertHandler implements PendingActionHandler {

    private final CompteService compteService;
    private final ObjectMapper objectMapper;

    public ModifyDecouvertHandler(CompteService compteService, ObjectMapper objectMapper) {
        this.compteService = compteService;
        this.objectMapper = objectMapper;
    }

    @Override
    public String execute(ActionEnAttente action, Map<String, Object> payload) {
        ChangementDecouvertRequestDTO dto = objectMapper.convertValue(payload, ChangementDecouvertRequestDTO.class);
        return compteService.changerDecouvertAutorise(dto.getNumCompte(), dto.getNouveauPlafond()).getNumCompte();
    }

    @Override
    public String getTypeAction() {
        return "MODIFY_DECOUVERT";
    }
}
