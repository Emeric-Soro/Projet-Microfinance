package com.soutra.microfinance.dto.request.mobile;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MobileRefreshTokenRequestDTO {

    @NotBlank(message = "Le refresh token est obligatoire")
    private String refreshToken;
}
