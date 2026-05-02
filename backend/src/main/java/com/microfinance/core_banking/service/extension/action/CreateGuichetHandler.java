package com.microfinance.core_banking.service.extension.action;

import com.microfinance.core_banking.dto.request.extension.CreerGuichetRequestDTO;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.service.extension.OrganisationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class CreateGuichetHandler implements PendingActionHandler {
    private final OrganisationService organisationService;
    private final ObjectMapper objectMapper;
    public CreateGuichetHandler(OrganisationService organisationService, ObjectMapper objectMapper) {
        this.organisationService = organisationService;
        this.objectMapper = objectMapper;
    }
    @Override
    public String execute(ActionEnAttente action, Map<String, Object> payload) {
        CreerGuichetRequestDTO dto = objectMapper.convertValue(payload, CreerGuichetRequestDTO.class);
        return String.valueOf(organisationService.creerGuichet(dto).getIdGuichet());
    }
    @Override
    public String getTypeAction() { return "CREATE_GUICHET"; }
}
