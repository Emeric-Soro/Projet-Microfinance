package com.microfinance.core_banking.mapper;

import com.microfinance.core_banking.dto.response.client.UtilisateurResponseDTO;
import com.microfinance.core_banking.entity.RoleUtilisateur;
import com.microfinance.core_banking.entity.Utilisateur;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UtilisateurMapper {

    // On va chercher l'ID du client à l'intérieur de l'objet Client
    @Mapping(source = "client.idClient", target = "idClient")
    UtilisateurResponseDTO toResponseDTO(Utilisateur utilisateur);

    // Astuce MapStruct : En ajoutant cette méthode, MapStruct va comprendre tout seul
    // comment transformer la List<RoleUtilisateur> en List<String> !
    default String mapRoleToString(RoleUtilisateur role) {
        if (role == null) return null;
        return role.getCodeRoleUtilisateur();
    }
}