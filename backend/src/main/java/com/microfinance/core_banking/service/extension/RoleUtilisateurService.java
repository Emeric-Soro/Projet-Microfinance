package com.microfinance.core_banking.service.extension;

import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.entity.RoleUtilisateur;

import java.util.List;

public interface RoleUtilisateurService {
    List<RoleUtilisateur> getAllRoles();
    ActionEnAttente submitCreateRole(String code, String intitule, String commentaireMaker);
    ActionEnAttente submitUpdateRole(Long idRole, String code, String intitule, String commentaireMaker);
    ActionEnAttente submitDeleteRole(Long idRole, String commentaireMaker);
    RoleUtilisateur applyCreateRole(String code, String intitule);
    RoleUtilisateur applyUpdateRole(Long idRole, String code, String intitule);
    void applyDeleteRole(Long idRole);
}
