package com.microfinance.core_banking.service.extension.action;

import com.microfinance.core_banking.dto.request.extension.CreerCaisseServiceRequestDTO;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.service.extension.TresorerieService;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class CreateCaisseHandler implements PendingActionHandler {
    private final TresorerieService tresorerieService;
    public CreateCaisseHandler(TresorerieService tresorerieService) {
        this.tresorerieService = tresorerieService;
    }
    @Override
    public String execute(ActionEnAttente action, Map<String, Object> payload) {
        CreerCaisseServiceRequestDTO dto = CreerCaisseServiceRequestDTO.fromMap(payload);
        return String.valueOf(tresorerieService.creerCaisse(dto).getIdCaisse());
    }
    @Override
    public String getTypeAction() { return "CREATE_CAISSE"; }
}
