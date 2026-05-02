package com.microfinance.core_banking.service.extension.action;

import com.microfinance.core_banking.dto.request.extension.CreerOrdrePaiementServiceRequestDTO;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.service.extension.PaiementExterneService;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class CreateOrdrePaiementExterneHandler implements PendingActionHandler {
    private final PaiementExterneService paiementExterneService;
    public CreateOrdrePaiementExterneHandler(PaiementExterneService paiementExterneService) {
        this.paiementExterneService = paiementExterneService;
    }
    @Override
    public String execute(ActionEnAttente action, Map<String, Object> payload) {
        CreerOrdrePaiementServiceRequestDTO dto = CreerOrdrePaiementServiceRequestDTO.fromMap(payload);
        return String.valueOf(paiementExterneService.initierOrdrePaiement(dto).getIdOrdrePaiementExterne());
    }
    @Override
    public String getTypeAction() { return "CREATE_ORDRE_PAIEMENT_EXTERNE"; }
}
