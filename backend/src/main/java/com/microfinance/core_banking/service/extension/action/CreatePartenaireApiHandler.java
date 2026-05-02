package com.microfinance.core_banking.service.extension.action;

import com.microfinance.core_banking.dto.request.extension.CreerPartenaireServiceRequestDTO;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.service.extension.DigitalExtensionService;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class CreatePartenaireApiHandler implements PendingActionHandler {
    private final DigitalExtensionService digitalExtensionService;
    public CreatePartenaireApiHandler(DigitalExtensionService digitalExtensionService) {
        this.digitalExtensionService = digitalExtensionService;
    }
    @Override
    public String execute(ActionEnAttente action, Map<String, Object> payload) {
        CreerPartenaireServiceRequestDTO dto = CreerPartenaireServiceRequestDTO.fromMap(payload);
        return String.valueOf(digitalExtensionService.creerPartenaire(dto).getIdPartenaireApi());
    }
    @Override
    public String getTypeAction() { return "CREATE_PARTENAIRE_API"; }
}
