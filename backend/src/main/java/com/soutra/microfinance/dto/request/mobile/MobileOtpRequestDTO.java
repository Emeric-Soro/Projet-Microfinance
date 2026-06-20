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
public class MobileOtpRequestDTO {

    @NotBlank(message = "Le challengeId est obligatoire")
    private String challengeId;

    @NotBlank(message = "Le code OTP est obligatoire")
    private String codeOtp;
}
