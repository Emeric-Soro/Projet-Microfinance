package com.microfinance.core_banking.service.extension.action;

import com.microfinance.core_banking.dto.request.extension.GenererRapportFiscalServiceRequestDTO;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.service.extension.ConformiteExtensionService;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class RapportFiscalHandler implements PendingActionHandler {
    private final ConformiteExtensionService conformiteExtensionService;
    public RapportFiscalHandler(ConformiteExtensionService conformiteExtensionService) {
        this.conformiteExtensionService = conformiteExtensionService;
    }
    @Override
    public String execute(ActionEnAttente action, Map<String, Object> payload) {
        GenererRapportFiscalServiceRequestDTO dto = GenererRapportFiscalServiceRequestDTO.fromMap(payload);
        return String.valueOf(conformiteExtensionService.genererRapportFiscal(dto).getIdRapportReglementaire());
    }
    @Override
    public String getTypeAction() { return "RAPPORT_FISCAL"; }
}
