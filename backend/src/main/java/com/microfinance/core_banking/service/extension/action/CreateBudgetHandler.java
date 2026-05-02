package com.microfinance.core_banking.service.extension.action;

import com.microfinance.core_banking.dto.request.extension.CreerBudgetServiceRequestDTO;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.service.extension.SupportEntrepriseService;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class CreateBudgetHandler implements PendingActionHandler {
    private final SupportEntrepriseService supportEntrepriseService;
    public CreateBudgetHandler(SupportEntrepriseService supportEntrepriseService) {
        this.supportEntrepriseService = supportEntrepriseService;
    }
    @Override
    public String execute(ActionEnAttente action, Map<String, Object> payload) {
        CreerBudgetServiceRequestDTO dto = CreerBudgetServiceRequestDTO.fromMap(payload);
        return String.valueOf(supportEntrepriseService.creerBudget(dto).getIdBudget());
    }
    @Override
    public String getTypeAction() { return "CREATE_BUDGET"; }
}
