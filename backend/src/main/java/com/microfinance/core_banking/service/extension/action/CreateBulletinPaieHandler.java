package com.microfinance.core_banking.service.extension.action;

import com.microfinance.core_banking.dto.request.extension.GenererBulletinPaieServiceRequestDTO;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.service.extension.SupportEntrepriseService;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class CreateBulletinPaieHandler implements PendingActionHandler {
    private final SupportEntrepriseService supportEntrepriseService;
    public CreateBulletinPaieHandler(SupportEntrepriseService supportEntrepriseService) {
        this.supportEntrepriseService = supportEntrepriseService;
    }
    @Override
    public String execute(ActionEnAttente action, Map<String, Object> payload) {
        GenererBulletinPaieServiceRequestDTO dto = GenererBulletinPaieServiceRequestDTO.fromMap(payload);
        return String.valueOf(supportEntrepriseService.genererBulletinPaie(dto).getIdBulletinPaie());
    }
    @Override
    public String getTypeAction() { return "CREATE_BULLETIN_PAIE"; }
}
