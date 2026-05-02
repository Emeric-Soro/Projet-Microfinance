package com.microfinance.core_banking.service.extension.action;

import com.microfinance.core_banking.dto.request.extension.CreerParametreAgenceRequestDTO;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.service.extension.OrganisationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class CreateParametreAgenceHandler implements PendingActionHandler {
    private final OrganisationService organisationService;
    private final ObjectMapper objectMapper;
    public CreateParametreAgenceHandler(OrganisationService organisationService, ObjectMapper objectMapper) {
        this.organisationService = organisationService;
        this.objectMapper = objectMapper;
    }
    @Override
    public String execute(ActionEnAttente action, Map<String, Object> payload) {
        CreerParametreAgenceRequestDTO dto = objectMapper.convertValue(payload, CreerParametreAgenceRequestDTO.class);
        return String.valueOf(organisationService.creerParametreAgence(dto).getIdParametreAgence());
    }
    @Override
    public String getTypeAction() { return "CREATE_PARAMETRE_AGENCE"; }
}
