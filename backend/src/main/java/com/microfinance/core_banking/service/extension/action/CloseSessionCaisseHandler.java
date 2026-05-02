package com.microfinance.core_banking.service.extension.action;

import com.microfinance.core_banking.dto.request.extension.FermerSessionServiceRequestDTO;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.service.extension.TresorerieService;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class CloseSessionCaisseHandler implements PendingActionHandler {
    private final TresorerieService tresorerieService;
    public CloseSessionCaisseHandler(TresorerieService tresorerieService) {
        this.tresorerieService = tresorerieService;
    }
    @Override
    public String execute(ActionEnAttente action, Map<String, Object> payload) {
        FermerSessionServiceRequestDTO dto = FermerSessionServiceRequestDTO.fromMap(payload);
        return String.valueOf(tresorerieService.fermerSession(
                Long.valueOf(action.getReferenceRessource()), dto).getIdSessionCaisse());
    }
    @Override
    public String getTypeAction() { return "CLOSE_SESSION_CAISSE"; }
}
