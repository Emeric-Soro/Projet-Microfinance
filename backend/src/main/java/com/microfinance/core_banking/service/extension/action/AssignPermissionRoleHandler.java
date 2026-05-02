package com.microfinance.core_banking.service.extension.action;

import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.service.extension.PermissionSecuriteService;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class AssignPermissionRoleHandler implements PendingActionHandler {
    private final PermissionSecuriteService permissionSecuriteService;
    public AssignPermissionRoleHandler(PermissionSecuriteService permissionSecuriteService) {
        this.permissionSecuriteService = permissionSecuriteService;
    }
    @Override
    public String execute(ActionEnAttente action, Map<String, Object> payload) {
        return String.valueOf(permissionSecuriteService.appliquerAffectationRole(
                Long.valueOf(payload.get("idRole").toString()),
                Long.valueOf(payload.get("idPermission").toString())).getIdRole());
    }
    @Override
    public String getTypeAction() { return "ASSIGN_PERMISSION_ROLE"; }
}
