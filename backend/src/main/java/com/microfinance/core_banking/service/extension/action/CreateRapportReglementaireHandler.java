package com.microfinance.core_banking.service.extension.action;

import com.microfinance.core_banking.dto.request.extension.CreerRapportServiceRequestDTO;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.service.extension.ConformiteExtensionService;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class CreateRapportReglementaireHandler implements PendingActionHandler {
    private final ConformiteExtensionService conformiteExtensionService;
    public CreateRapportReglementaireHandler(ConformiteExtensionService conformiteExtensionService) {
        this.conformiteExtensionService = conformiteExtensionService;
    }
    @Override
    public String execute(ActionEnAttente action, Map<String, Object> payload) {
        CreerRapportServiceRequestDTO dto = CreerRapportServiceRequestDTO.fromMap(payload);
        return String.valueOf(conformiteExtensionService.creerRapport(dto).getIdRapportReglementaire());
    }
    @Override
    public String getTypeAction() { return "CREATE_RAPPORT_REGLEMENTAIRE"; }
}
