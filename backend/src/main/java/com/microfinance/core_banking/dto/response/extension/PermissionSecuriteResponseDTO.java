package com.microfinance.core_banking.dto.response.extension;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PermissionSecuriteResponseDTO {
    private Long idPermission;
    private String codePermission;
    private String libellePermission;
    private String moduleCode;
    private String descriptionPermission;
    private Boolean actif;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
