package com.microfinance.core_banking.service.extension.action;

import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.service.extension.RoleUtilisateurService;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class UpdateRoleHandler implements PendingActionHandler {
    private final RoleUtilisateurService roleUtilisateurService;
    public UpdateRoleHandler(RoleUtilisateurService roleUtilisateurService) {
        this.roleUtilisateurService = roleUtilisateurService;
    }
    @Override
    public String execute(ActionEnAttente action, Map<String, Object> payload) {
        return String.valueOf(roleUtilisateurService.applyUpdateRole(
                Long.valueOf(action.getReferenceRessource()),
                payload.get("codeRoleUtilisateur").toString(),
                payload.get("intituleRole").toString()).getIdRole());
    }
    @Override
    public String getTypeAction() { return "UPDATE_ROLE"; }
}
