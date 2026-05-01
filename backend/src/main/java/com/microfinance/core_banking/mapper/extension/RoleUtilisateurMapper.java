package com.microfinance.core_banking.mapper.extension;

import com.microfinance.core_banking.dto.response.extension.RoleUtilisateurResponseDTO;
import com.microfinance.core_banking.entity.PermissionSecurite;
import com.microfinance.core_banking.entity.RoleUtilisateur;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoleUtilisateurMapper {

    @Mapping(target = "permissions", expression = "java(toPermissionCodes(entity.getPermissions()))")
    RoleUtilisateurResponseDTO toDto(RoleUtilisateur entity);
    List<RoleUtilisateurResponseDTO> toDtoList(List<RoleUtilisateur> entities);

    default Set<String> toPermissionCodes(Set<PermissionSecurite> permissions) {
        if (permissions == null) {
            return Set.of();
        }
        return permissions.stream()
                .map(PermissionSecurite::getCodePermission)
                .collect(Collectors.toCollection(java.util.LinkedHashSet::new));
    }
}
