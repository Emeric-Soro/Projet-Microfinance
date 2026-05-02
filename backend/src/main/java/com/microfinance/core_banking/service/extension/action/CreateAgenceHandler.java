package com.microfinance.core_banking.service.extension.action;

import com.microfinance.core_banking.dto.request.extension.CreerAgenceRequestDTO;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.service.extension.OrganisationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class CreateAgenceHandler implements PendingActionHandler {
    private final OrganisationService organisationService;
    private final ObjectMapper objectMapper;
    public CreateAgenceHandler(OrganisationService organisationService, ObjectMapper objectMapper) {
        this.organisationService = organisationService;
        this.objectMapper = objectMapper;
    }
    @Override
    public String execute(ActionEnAttente action, Map<String, Object> payload) {
        CreerAgenceRequestDTO dto = objectMapper.convertValue(payload, CreerAgenceRequestDTO.class);
        return String.valueOf(organisationService.creerAgence(dto).getIdAgence());
    }
    @Override
    public String getTypeAction() { return "CREATE_AGENCE"; }
}
