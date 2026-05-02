package com.microfinance.core_banking.service.extension.action;

import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.service.extension.PermissionSecuriteService;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class RevokePermissionRoleHandler implements PendingActionHandler {
    private final PermissionSecuriteService permissionSecuriteService;
    public RevokePermissionRoleHandler(PermissionSecuriteService permissionSecuriteService) {
        this.permissionSecuriteService = permissionSecuriteService;
    }
    @Override
    public String execute(ActionEnAttente action, Map<String, Object> payload) {
        return String.valueOf(permissionSecuriteService.appliquerRevocationRole(
                Long.valueOf(payload.get("idRole").toString()),
                Long.valueOf(payload.get("idPermission").toString())).getIdRole());
    }
    @Override
    public String getTypeAction() { return "REVOKE_PERMISSION_ROLE"; }
}
