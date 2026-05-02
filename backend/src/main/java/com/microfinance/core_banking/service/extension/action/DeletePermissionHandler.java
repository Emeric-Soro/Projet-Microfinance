package com.microfinance.core_banking.service.extension.action;

import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.service.extension.PermissionSecuriteService;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class DeletePermissionHandler implements PendingActionHandler {
    private final PermissionSecuriteService permissionSecuriteService;
    public DeletePermissionHandler(PermissionSecuriteService permissionSecuriteService) {
        this.permissionSecuriteService = permissionSecuriteService;
    }
    @Override
    public String execute(ActionEnAttente action, Map<String, Object> payload) {
        permissionSecuriteService.appliquerSuppression(Long.valueOf(action.getReferenceRessource()));
        return action.getReferenceRessource();
    }
    @Override
    public String getTypeAction() { return "DELETE_PERMISSION"; }
}
