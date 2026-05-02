package com.microfinance.core_banking.service.extension.action;

import com.microfinance.core_banking.dto.request.extension.OuvrirSessionServiceRequestDTO;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.service.extension.TresorerieService;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class OpenSessionCaisseHandler implements PendingActionHandler {
    private final TresorerieService tresorerieService;
    public OpenSessionCaisseHandler(TresorerieService tresorerieService) {
        this.tresorerieService = tresorerieService;
    }
    @Override
    public String execute(ActionEnAttente action, Map<String, Object> payload) {
        OuvrirSessionServiceRequestDTO dto = OuvrirSessionServiceRequestDTO.fromMap(payload);
        return String.valueOf(tresorerieService.ouvrirSession(dto).getIdSessionCaisse());
    }
    @Override
    public String getTypeAction() { return "OPEN_SESSION_CAISSE"; }
}
