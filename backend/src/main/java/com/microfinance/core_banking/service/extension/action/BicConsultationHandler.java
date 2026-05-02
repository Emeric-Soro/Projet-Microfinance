package com.microfinance.core_banking.service.extension.action;

import com.microfinance.core_banking.dto.request.extension.ConsulterBicServiceRequestDTO;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.service.extension.ConformiteExtensionService;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class BicConsultationHandler implements PendingActionHandler {
    private final ConformiteExtensionService conformiteExtensionService;
    public BicConsultationHandler(ConformiteExtensionService conformiteExtensionService) {
        this.conformiteExtensionService = conformiteExtensionService;
    }
    @Override
    public String execute(ActionEnAttente action, Map<String, Object> payload) {
        ConsulterBicServiceRequestDTO dto = ConsulterBicServiceRequestDTO.fromMap(payload);
        return String.valueOf(conformiteExtensionService.enregistrerConsultationBic(dto).getIdRapportReglementaire());
    }
    @Override
    public String getTypeAction() { return "BIC_CONSULTATION"; }
}
