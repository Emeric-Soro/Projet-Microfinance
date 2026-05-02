package com.microfinance.core_banking.service.extension.action;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microfinance.core_banking.dto.request.compte.OuvertureCompteRequestDTO;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.service.compte.CompteService;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class OpenCompteSensibleHandler implements PendingActionHandler {

    private final CompteService compteService;
    private final ObjectMapper objectMapper;

    public OpenCompteSensibleHandler(CompteService compteService, ObjectMapper objectMapper) {
        this.compteService = compteService;
        this.objectMapper = objectMapper;
    }

    @Override
    public String execute(ActionEnAttente action, Map<String, Object> payload) {
        OuvertureCompteRequestDTO dto = objectMapper.convertValue(payload, OuvertureCompteRequestDTO.class);
        return compteService.ouvrirCompte(dto.getIdClient(), dto.getCodeTypeCompte(), dto.getDepotInitial()).getNumCompte();
    }

    @Override
    public String getTypeAction() {
        return "OPEN_COMPTE_SENSIBLE";
    }
}
