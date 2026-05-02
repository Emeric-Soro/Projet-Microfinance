package com.microfinance.core_banking.service.extension.action;

import com.microfinance.core_banking.dto.request.extension.GenererRapportPrudentielServiceRequestDTO;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.service.extension.ConformiteExtensionService;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class RapportPrudentielHandler implements PendingActionHandler {
    private final ConformiteExtensionService conformiteExtensionService;
    public RapportPrudentielHandler(ConformiteExtensionService conformiteExtensionService) {
        this.conformiteExtensionService = conformiteExtensionService;
    }
    @Override
    public String execute(ActionEnAttente action, Map<String, Object> payload) {
        GenererRapportPrudentielServiceRequestDTO dto = GenererRapportPrudentielServiceRequestDTO.fromMap(payload);
        return String.valueOf(conformiteExtensionService.genererRapportPrudentiel(dto).getIdRapportReglementaire());
    }
    @Override
    public String getTypeAction() { return "RAPPORT_PRUDENTIEL"; }
}
