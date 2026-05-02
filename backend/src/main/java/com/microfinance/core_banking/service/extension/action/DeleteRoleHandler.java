package com.microfinance.core_banking.service.extension.action;

import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.service.extension.RoleUtilisateurService;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class DeleteRoleHandler implements PendingActionHandler {
    private final RoleUtilisateurService roleUtilisateurService;
    public DeleteRoleHandler(RoleUtilisateurService roleUtilisateurService) {
        this.roleUtilisateurService = roleUtilisateurService;
    }
    @Override
    public String execute(ActionEnAttente action, Map<String, Object> payload) {
        roleUtilisateurService.applyDeleteRole(Long.valueOf(action.getReferenceRessource()));
        return action.getReferenceRessource();
    }
    @Override
    public String getTypeAction() { return "DELETE_ROLE"; }
}
