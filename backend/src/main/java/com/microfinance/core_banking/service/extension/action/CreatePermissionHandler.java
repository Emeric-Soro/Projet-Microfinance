package com.microfinance.core_banking.service.extension.action;

import com.microfinance.core_banking.dto.request.extension.PermissionSecuriteRequestDTO;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.service.extension.PermissionSecuriteService;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class CreatePermissionHandler implements PendingActionHandler {
    private final PermissionSecuriteService permissionSecuriteService;
    public CreatePermissionHandler(PermissionSecuriteService permissionSecuriteService) {
        this.permissionSecuriteService = permissionSecuriteService;
    }
    @Override
    public String execute(ActionEnAttente action, Map<String, Object> payload) {
        PermissionSecuriteRequestDTO dto = PermissionSecuriteRequestDTO.fromMap(payload);
        return String.valueOf(permissionSecuriteService.appliquerCreation(dto).getIdPermission());
    }
    @Override
    public String getTypeAction() { return "CREATE_PERMISSION"; }
}
