package com.microfinance.core_banking.config;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Getter
@Setter
@Validated
@Component
@ConfigurationProperties(prefix = "app.security.auth")
public class AuthSecurityProperties {

    @Positive
    private int maxFailedAttempts = 5;

    @NotNull
    private Duration lockDuration = Duration.ofMinutes(15);

    @Positive
    private int credentialsValidityDays = 90;

    @NotNull
    private Duration otpValidity = Duration.ofMinutes(5);

    @Positive
    private int otpLength = 6;

    @Positive
    private int maxOtpAttempts = 3;

    @Positive
    private int passwordHistoryDepth = 5;
}
