package com.microfinance.core_banking.service.extension.action;

import com.microfinance.core_banking.dto.request.extension.CreerTransactionMobileMoneyServiceRequestDTO;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.service.extension.PaiementExterneService;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class CreateTransactionMobileMoneyHandler implements PendingActionHandler {
    private final PaiementExterneService paiementExterneService;
    public CreateTransactionMobileMoneyHandler(PaiementExterneService paiementExterneService) {
        this.paiementExterneService = paiementExterneService;
    }
    @Override
    public String execute(ActionEnAttente action, Map<String, Object> payload) {
        CreerTransactionMobileMoneyServiceRequestDTO dto = CreerTransactionMobileMoneyServiceRequestDTO.fromMap(payload);
        return String.valueOf(paiementExterneService.enregistrerTransactionMobileMoney(dto).getIdTransactionMobileMoney());
    }
    @Override
    public String getTypeAction() { return "CREATE_TRANSACTION_MOBILE_MONEY"; }
}
