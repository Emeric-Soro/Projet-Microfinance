package com.microfinance.core_banking.service.extension.action;

import com.microfinance.core_banking.dto.request.extension.CreerMutationRequestDTO;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.service.extension.OrganisationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class CreateMutationPersonnelHandler implements PendingActionHandler {
    private final OrganisationService organisationService;
    private final ObjectMapper objectMapper;
    public CreateMutationPersonnelHandler(OrganisationService organisationService, ObjectMapper objectMapper) {
        this.organisationService = organisationService;
        this.objectMapper = objectMapper;
    }
    @Override
    public String execute(ActionEnAttente action, Map<String, Object> payload) {
        CreerMutationRequestDTO dto = objectMapper.convertValue(payload, CreerMutationRequestDTO.class);
        return String.valueOf(organisationService.creerMutationPersonnel(dto).getIdMutationPersonnel());
    }
    @Override
    public String getTypeAction() { return "CREATE_MUTATION_PERSONNEL"; }
}
