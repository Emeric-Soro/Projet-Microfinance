package com.microfinance.core_banking.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.demo-user")
public class DemoUserProperties {

    private boolean enabled = false;

    private String login = "demo.admin";

    private String password = "Demo@12345";

    private String clientCode = "CLI-DEMO-0001";

    private String clientStatus = "ACTIF";

    private String roleCode = "ADMIN";

    private String roleLabel = "Administrateur de demonstration";

    private String nom = "Demo";

    private String prenom = "Admin";

    private LocalDate dateNaissance = LocalDate.of(1990, 1, 1);

    private String email = "demo.admin@microfin.local";

    private String telephone = "700000001";

    private boolean secondFactorEnabled = false;
}
