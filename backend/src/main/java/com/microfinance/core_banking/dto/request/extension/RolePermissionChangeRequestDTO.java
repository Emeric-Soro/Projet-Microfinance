package com.microfinance.core_banking.dto.request.extension;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RolePermissionChangeRequestDTO {

    @Size(max = 500, message = "Le commentaire maker ne doit pas depasser 500 caracteres")
    private String commentaireMaker;
}
