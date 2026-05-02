package com.microfinance.core_banking.service.extension.action;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microfinance.core_banking.dto.request.compte.ClotureCompteRequestDTO;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.service.compte.CompteService;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ClotureCompteHandler implements PendingActionHandler {

    private final CompteService compteService;
    private final ObjectMapper objectMapper;

    public ClotureCompteHandler(CompteService compteService, ObjectMapper objectMapper) {
        this.compteService = compteService;
        this.objectMapper = objectMapper;
    }

    @Override
    public String execute(ActionEnAttente action, Map<String, Object> payload) {
        ClotureCompteRequestDTO dto = objectMapper.convertValue(payload, ClotureCompteRequestDTO.class);
        return compteService.cloturerCompte(dto.getNumCompte()).getNumCompte();
    }

    @Override
    public String getTypeAction() {
        return "CLOTURE_COMPTE";
    }
}
