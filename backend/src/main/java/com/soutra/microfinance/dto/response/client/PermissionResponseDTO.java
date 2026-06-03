package com.soutra.microfinance.dto.response.client;

import java.util.Set;

public record PermissionResponseDTO(
        String codeRole,
        Set<String> permissions
) {}
