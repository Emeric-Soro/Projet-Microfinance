package com.microfinance.core_banking.service.extension.action;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microfinance.core_banking.dto.request.compte.ChangementStatutCompteRequestDTO;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.service.compte.CompteService;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class BlockCompteHandler implements PendingActionHandler {

    private final CompteService compteService;
    private final ObjectMapper objectMapper;

    public BlockCompteHandler(CompteService compteService, ObjectMapper objectMapper) {
        this.compteService = compteService;
        this.objectMapper = objectMapper;
    }

    @Override
    public String execute(ActionEnAttente action, Map<String, Object> payload) {
        ChangementStatutCompteRequestDTO dto = objectMapper.convertValue(payload, ChangementStatutCompteRequestDTO.class);
        return compteService.bloquerCompte(dto.getNumCompte(), dto.getMotif()).getNumCompte();
    }

    @Override
    public String getTypeAction() {
        return "BLOCK_COMPTE";
    }
}
