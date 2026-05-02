package com.microfinance.core_banking.service.extension.action;

import com.microfinance.core_banking.dto.request.extension.CreerCoffreServiceRequestDTO;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.service.extension.TresorerieService;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class CreateCoffreHandler implements PendingActionHandler {
    private final TresorerieService tresorerieService;
    public CreateCoffreHandler(TresorerieService tresorerieService) {
        this.tresorerieService = tresorerieService;
    }
    @Override
    public String execute(ActionEnAttente action, Map<String, Object> payload) {
        CreerCoffreServiceRequestDTO dto = CreerCoffreServiceRequestDTO.fromMap(payload);
        return String.valueOf(tresorerieService.creerCoffre(dto).getIdCoffre());
    }
    @Override
    public String getTypeAction() { return "CREATE_COFFRE"; }
}
