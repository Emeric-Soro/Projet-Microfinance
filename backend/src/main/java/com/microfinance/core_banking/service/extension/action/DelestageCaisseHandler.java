package com.microfinance.core_banking.service.extension.action;

import com.microfinance.core_banking.dto.request.extension.DelesterCaisseServiceRequestDTO;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.service.extension.TresorerieService;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class DelestageCaisseHandler implements PendingActionHandler {
    private final TresorerieService tresorerieService;
    public DelestageCaisseHandler(TresorerieService tresorerieService) {
        this.tresorerieService = tresorerieService;
    }
    @Override
    public String execute(ActionEnAttente action, Map<String, Object> payload) {
        DelesterCaisseServiceRequestDTO dto = DelesterCaisseServiceRequestDTO.fromMap(payload);
        return String.valueOf(tresorerieService.delesterCaisse(dto).getIdDelestageCaisse());
    }
    @Override
    public String getTypeAction() { return "DELESTAGE_CAISSE"; }
}
