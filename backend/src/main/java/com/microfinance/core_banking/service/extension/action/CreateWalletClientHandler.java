package com.microfinance.core_banking.service.extension.action;

import com.microfinance.core_banking.dto.request.extension.CreerWalletServiceRequestDTO;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.service.extension.PaiementExterneService;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class CreateWalletClientHandler implements PendingActionHandler {
    private final PaiementExterneService paiementExterneService;
    public CreateWalletClientHandler(PaiementExterneService paiementExterneService) {
        this.paiementExterneService = paiementExterneService;
    }
    @Override
    public String execute(ActionEnAttente action, Map<String, Object> payload) {
        CreerWalletServiceRequestDTO dto = CreerWalletServiceRequestDTO.fromMap(payload);
        return String.valueOf(paiementExterneService.creerWallet(dto).getIdWalletClient());
    }
    @Override
    public String getTypeAction() { return "CREATE_WALLET_CLIENT"; }
}
