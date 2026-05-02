package com.microfinance.core_banking.service.extension.action;

import com.microfinance.core_banking.dto.request.extension.CreerCommandeServiceRequestDTO;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.service.extension.SupportEntrepriseService;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class CreateCommandeAchatHandler implements PendingActionHandler {
    private final SupportEntrepriseService supportEntrepriseService;
    public CreateCommandeAchatHandler(SupportEntrepriseService supportEntrepriseService) {
        this.supportEntrepriseService = supportEntrepriseService;
    }
    @Override
    public String execute(ActionEnAttente action, Map<String, Object> payload) {
        CreerCommandeServiceRequestDTO dto = CreerCommandeServiceRequestDTO.fromMap(payload);
        return String.valueOf(supportEntrepriseService.creerCommandeAchat(dto).getIdCommandeAchat());
    }
    @Override
    public String getTypeAction() { return "CREATE_COMMANDE_ACHAT"; }
}
