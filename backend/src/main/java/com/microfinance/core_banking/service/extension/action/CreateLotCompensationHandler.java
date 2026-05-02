package com.microfinance.core_banking.service.extension.action;

import com.microfinance.core_banking.dto.request.extension.CreerLotCompensationServiceRequestDTO;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.service.extension.PaiementExterneService;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class CreateLotCompensationHandler implements PendingActionHandler {
    private final PaiementExterneService paiementExterneService;
    public CreateLotCompensationHandler(PaiementExterneService paiementExterneService) {
        this.paiementExterneService = paiementExterneService;
    }
    @Override
    public String execute(ActionEnAttente action, Map<String, Object> payload) {
        CreerLotCompensationServiceRequestDTO dto = CreerLotCompensationServiceRequestDTO.fromMap(payload);
        return String.valueOf(paiementExterneService.creerLotCompensation(dto).getIdLotCompensation());
    }
    @Override
    public String getTypeAction() { return "CREATE_LOT_COMPENSATION"; }
}
