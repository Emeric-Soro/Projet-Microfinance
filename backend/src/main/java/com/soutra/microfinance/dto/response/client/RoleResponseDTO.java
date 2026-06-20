package com.soutra.microfinance.dto.response.client;

import java.time.LocalDateTime;

public record RoleResponseDTO(
        Long idRole,
        String codeRoleUtilisateur,
        String intituleRole,
        Integer nombreUtilisateurs,
        LocalDateTime dateCreation,
        LocalDateTime derniereModification
) {}
