package com.microfinance.core_banking.service.extension.action;

import com.microfinance.core_banking.dto.request.extension.ChangerStatutTransactionMobileMoneyRequestDTO;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.service.extension.PaiementExterneService;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class UpdateStatutTransactionMobileMoneyHandler implements PendingActionHandler {
    private final PaiementExterneService paiementExterneService;
    public UpdateStatutTransactionMobileMoneyHandler(PaiementExterneService paiementExterneService) {
        this.paiementExterneService = paiementExterneService;
    }
    @Override
    public String execute(ActionEnAttente action, Map<String, Object> payload) {
        ChangerStatutTransactionMobileMoneyRequestDTO dto = ChangerStatutTransactionMobileMoneyRequestDTO.fromMap(payload);
        return String.valueOf(paiementExterneService.changerStatutTransactionMobileMoney(
                Long.valueOf(action.getReferenceRessource()), dto).getIdTransactionMobileMoney());
    }
    @Override
    public String getTypeAction() { return "UPDATE_STATUT_TRANSACTION_MOBILE_MONEY"; }
}
