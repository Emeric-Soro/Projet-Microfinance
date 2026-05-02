package com.microfinance.core_banking.service.extension.action;

import com.microfinance.core_banking.dto.request.extension.AffecterUtilisateurRequestDTO;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.service.extension.OrganisationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class AssignUserAgenceHandler implements PendingActionHandler {
    private final OrganisationService organisationService;
    private final ObjectMapper objectMapper;
    public AssignUserAgenceHandler(OrganisationService organisationService, ObjectMapper objectMapper) {
        this.organisationService = organisationService;
        this.objectMapper = objectMapper;
    }
    @Override
    public String execute(ActionEnAttente action, Map<String, Object> payload) {
        AffecterUtilisateurRequestDTO dto = objectMapper.convertValue(payload, AffecterUtilisateurRequestDTO.class);
        return String.valueOf(organisationService.affecterUtilisateur(dto).getIdAffectation());
    }
    @Override
    public String getTypeAction() { return "ASSIGN_USER_AGENCE"; }
}
