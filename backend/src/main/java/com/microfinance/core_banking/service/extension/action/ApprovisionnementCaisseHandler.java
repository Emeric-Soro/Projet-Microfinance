package com.microfinance.core_banking.service.extension.action;

import com.microfinance.core_banking.dto.request.extension.ApprovisionnerCaisseServiceRequestDTO;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.service.extension.TresorerieService;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class ApprovisionnementCaisseHandler implements PendingActionHandler {
    private final TresorerieService tresorerieService;
    public ApprovisionnementCaisseHandler(TresorerieService tresorerieService) {
        this.tresorerieService = tresorerieService;
    }
    @Override
    public String execute(ActionEnAttente action, Map<String, Object> payload) {
        ApprovisionnerCaisseServiceRequestDTO dto = ApprovisionnerCaisseServiceRequestDTO.fromMap(payload);
        return String.valueOf(tresorerieService.approvisionnerCaisse(dto).getIdApprovisionnementCaisse());
    }
    @Override
    public String getTypeAction() { return "APPROVISIONNEMENT_CAISSE"; }
}
