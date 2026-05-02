package com.microfinance.core_banking.service.extension.action;

import com.microfinance.core_banking.dto.request.extension.ChangerStatutOrdreServiceRequestDTO;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.service.extension.PaiementExterneService;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class UpdateStatutOrdrePaiementExterneHandler implements PendingActionHandler {
    private final PaiementExterneService paiementExterneService;
    public UpdateStatutOrdrePaiementExterneHandler(PaiementExterneService paiementExterneService) {
        this.paiementExterneService = paiementExterneService;
    }
    @Override
    public String execute(ActionEnAttente action, Map<String, Object> payload) {
        ChangerStatutOrdreServiceRequestDTO dto = ChangerStatutOrdreServiceRequestDTO.fromMap(payload);
        return String.valueOf(paiementExterneService.changerStatutOrdre(
                Long.valueOf(action.getReferenceRessource()), dto).getIdOrdrePaiementExterne());
    }
    @Override
    public String getTypeAction() { return "UPDATE_STATUT_ORDRE_PAIEMENT_EXTERNE"; }
}
