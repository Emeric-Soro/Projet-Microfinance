package com.microfinance.core_banking.config;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;

@Getter
@Setter
@Validated
@Component
@ConfigurationProperties(prefix = "app.workflow.transaction")
public class TransactionWorkflowProperties {

    @NotNull
    @DecimalMin(value = "0.00")
    private BigDecimal approvalThreshold = new BigDecimal("500000.00");
}
