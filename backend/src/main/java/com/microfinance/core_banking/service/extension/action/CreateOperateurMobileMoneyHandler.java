package com.microfinance.core_banking.service.extension.action;

import com.microfinance.core_banking.dto.request.extension.CreerOperateurServiceRequestDTO;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.service.extension.PaiementExterneService;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class CreateOperateurMobileMoneyHandler implements PendingActionHandler {
    private final PaiementExterneService paiementExterneService;
    public CreateOperateurMobileMoneyHandler(PaiementExterneService paiementExterneService) {
        this.paiementExterneService = paiementExterneService;
    }
    @Override
    public String execute(ActionEnAttente action, Map<String, Object> payload) {
        CreerOperateurServiceRequestDTO dto = CreerOperateurServiceRequestDTO.fromMap(payload);
        return String.valueOf(paiementExterneService.creerOperateur(dto).getIdOperateurMobileMoney());
    }
    @Override
    public String getTypeAction() { return "CREATE_OPERATEUR_MOBILE_MONEY"; }
}
