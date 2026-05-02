package com.microfinance.core_banking.service.extension.action;

import com.microfinance.core_banking.dto.request.extension.CloturerDatRequestDTO;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.service.extension.EpargneExtensionService;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ClotureDatHandler implements PendingActionHandler {

    private final EpargneExtensionService epargneExtensionService;

    public ClotureDatHandler(EpargneExtensionService epargneExtensionService) {
        this.epargneExtensionService = epargneExtensionService;
    }

    @Override
    public String execute(ActionEnAttente action, Map<String, Object> payload) {
        Long idDat = Long.valueOf(action.getReferenceRessource());
        CloturerDatRequestDTO dto = CloturerDatRequestDTO.fromMap(payload);
        return epargneExtensionService.cloturerDepotATerme(idDat, dto).getReferenceDepot();
    }

    @Override
    public String getTypeAction() {
        return "CLOTURE_DAT";
    }
}
