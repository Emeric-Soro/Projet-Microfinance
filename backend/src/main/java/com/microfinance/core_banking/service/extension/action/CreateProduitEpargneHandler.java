package com.microfinance.core_banking.service.extension.action;

import com.microfinance.core_banking.dto.request.extension.CreerProduitEpargneServiceRequestDTO;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.service.extension.EpargneExtensionService;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class CreateProduitEpargneHandler implements PendingActionHandler {
    private final EpargneExtensionService epargneExtensionService;
    public CreateProduitEpargneHandler(EpargneExtensionService epargneExtensionService) {
        this.epargneExtensionService = epargneExtensionService;
    }
    @Override
    public String execute(ActionEnAttente action, Map<String, Object> payload) {
        CreerProduitEpargneServiceRequestDTO dto = CreerProduitEpargneServiceRequestDTO.fromMap(payload);
        return String.valueOf(epargneExtensionService.creerProduit(dto).getIdProduitEpargne());
    }
    @Override
    public String getTypeAction() { return "CREATE_PRODUIT_EPARGNE"; }
}
