package com.microfinance.core_banking.service.extension.action;

import com.microfinance.core_banking.dto.request.extension.CreerCompteLiaisonRequestDTO;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.service.extension.OrganisationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class CreateCompteLiaisonHandler implements PendingActionHandler {
    private final OrganisationService organisationService;
    private final ObjectMapper objectMapper;
    public CreateCompteLiaisonHandler(OrganisationService organisationService, ObjectMapper objectMapper) {
        this.organisationService = organisationService;
        this.objectMapper = objectMapper;
    }
    @Override
    public String execute(ActionEnAttente action, Map<String, Object> payload) {
        CreerCompteLiaisonRequestDTO dto = objectMapper.convertValue(payload, CreerCompteLiaisonRequestDTO.class);
        return String.valueOf(organisationService.creerCompteLiaison(dto).getIdCompteLiaisonAgence());
    }
    @Override
    public String getTypeAction() { return "CREATE_COMPTE_LIAISON_AGENCE"; }
}
