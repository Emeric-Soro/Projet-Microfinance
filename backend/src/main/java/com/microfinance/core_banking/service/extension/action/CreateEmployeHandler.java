package com.microfinance.core_banking.service.extension.action;

import com.microfinance.core_banking.dto.request.extension.CreerEmployeServiceRequestDTO;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.service.extension.DigitalExtensionService;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class CreateEmployeHandler implements PendingActionHandler {
    private final DigitalExtensionService digitalExtensionService;
    public CreateEmployeHandler(DigitalExtensionService digitalExtensionService) {
        this.digitalExtensionService = digitalExtensionService;
    }
    @Override
    public String execute(ActionEnAttente action, Map<String, Object> payload) {
        CreerEmployeServiceRequestDTO dto = CreerEmployeServiceRequestDTO.fromMap(payload);
        return String.valueOf(digitalExtensionService.creerEmploye(dto).getIdEmploye());
    }
    @Override
    public String getTypeAction() { return "CREATE_EMPLOYE"; }
}
