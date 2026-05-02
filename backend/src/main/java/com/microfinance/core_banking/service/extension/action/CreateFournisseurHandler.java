package com.microfinance.core_banking.service.extension.action;

import com.microfinance.core_banking.dto.request.extension.CreerFournisseurServiceRequestDTO;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.service.extension.SupportEntrepriseService;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class CreateFournisseurHandler implements PendingActionHandler {
    private final SupportEntrepriseService supportEntrepriseService;
    public CreateFournisseurHandler(SupportEntrepriseService supportEntrepriseService) {
        this.supportEntrepriseService = supportEntrepriseService;
    }
    @Override
    public String execute(ActionEnAttente action, Map<String, Object> payload) {
        CreerFournisseurServiceRequestDTO dto = CreerFournisseurServiceRequestDTO.fromMap(payload);
        return String.valueOf(supportEntrepriseService.creerFournisseur(dto).getIdFournisseur());
    }
    @Override
    public String getTypeAction() { return "CREATE_FOURNISSEUR"; }
}
