package com.microfinance.core_banking.service.extension.action;

import com.microfinance.core_banking.dto.request.extension.CreerImmobilisationServiceRequestDTO;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.service.extension.SupportEntrepriseService;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class CreateImmobilisationHandler implements PendingActionHandler {
    private final SupportEntrepriseService supportEntrepriseService;
    public CreateImmobilisationHandler(SupportEntrepriseService supportEntrepriseService) {
        this.supportEntrepriseService = supportEntrepriseService;
    }
    @Override
    public String execute(ActionEnAttente action, Map<String, Object> payload) {
        CreerImmobilisationServiceRequestDTO dto = CreerImmobilisationServiceRequestDTO.fromMap(payload);
        return String.valueOf(supportEntrepriseService.creerImmobilisation(dto).getIdImmobilisation());
    }
    @Override
    public String getTypeAction() { return "CREATE_IMMOBILISATION"; }
}
