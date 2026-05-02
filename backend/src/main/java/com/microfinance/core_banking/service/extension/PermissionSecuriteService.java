package com.microfinance.core_banking.service.extension;

import com.microfinance.core_banking.dto.request.extension.PermissionSecuriteRequestDTO;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.entity.PermissionSecurite;
import com.microfinance.core_banking.entity.RoleUtilisateur;

import java.util.List;

public interface PermissionSecuriteService {

    List<PermissionSecurite> rechercher(String moduleCode, Boolean actif);

    PermissionSecurite getById(Long idPermission);

    List<PermissionSecurite> listerPermissionsRole(Long idRole);

    ActionEnAttente soumettreCreation(String codePermission, String libellePermission, String moduleCode, String descriptionPermission, Boolean actif, String commentaireMaker);

    ActionEnAttente soumettreMiseAJour(Long idPermission, String codePermission, String libellePermission, String moduleCode, String descriptionPermission, Boolean actif, String commentaireMaker);

    ActionEnAttente soumettreSuppression(Long idPermission, String commentaireMaker);

    ActionEnAttente soumettreAffectationRole(Long idRole, Long idPermission, String commentaireMaker);

    ActionEnAttente soumettreRevocationRole(Long idRole, Long idPermission, String commentaireMaker);

    PermissionSecurite appliquerCreation(PermissionSecuriteRequestDTO dto);

    PermissionSecurite appliquerMiseAJour(Long idPermission, PermissionSecuriteRequestDTO dto);

    void appliquerSuppression(Long idPermission);

    RoleUtilisateur appliquerAffectationRole(Long idRole, Long idPermission);

    RoleUtilisateur appliquerRevocationRole(Long idRole, Long idPermission);
}
