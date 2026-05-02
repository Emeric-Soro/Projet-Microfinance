package com.microfinance.core_banking.workflow;

import com.microfinance.core_banking.service.security.SecurityConstants;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityApiTest {

    @Test
    void allRolesAreDefined() {
        Set<String> roles = Set.of(
            SecurityConstants.ROLE_ADMIN,
            SecurityConstants.ROLE_SUPERVISEUR,
            SecurityConstants.ROLE_GUICHETIER,
            SecurityConstants.ROLE_CLIENT,
            SecurityConstants.ROLE_CHEF_AGENCE,
            SecurityConstants.ROLE_MANAGER
        );

        assertThat(roles).hasSize(6);
        assertThat(roles).contains("ADMIN", "SUPERVISEUR", "GUICHETIER", "CLIENT", "CHEF_AGENCE", "MANAGER");
    }

    @Test
    void allSecurityConstantsAreFinal() {
        assertThat(java.lang.reflect.Modifier.isFinal(SecurityConstants.class.getModifiers())).isTrue();
    }

    @Test
    void allPermissionsStartWithProperPrefix() {
        Set<String> permissionFields = Arrays.stream(SecurityConstants.class.getDeclaredFields())
            .filter(f -> f.getName().startsWith("PERM_"))
            .map(f -> {
                try {
                    return (String) f.get(null);
                } catch (Exception e) {
                    return "";
                }
            })
            .collect(Collectors.toSet());

        assertThat(permissionFields).isNotEmpty();
        assertThat(permissionFields).allMatch(p -> p.matches("^[A-Z_]+$"));
    }

    @Test
    void authExpressionsContainValidRoles() {
        assertThat(SecurityConstants.AUTH_ADMIN).contains("ADMIN");
        assertThat(SecurityConstants.AUTH_ADMIN_SUPERVISEUR).contains("ADMIN", "SUPERVISEUR");
        assertThat(SecurityConstants.AUTH_ADMIN_GUICHETIER).contains("ADMIN", "GUICHETIER");
        assertThat(SecurityConstants.AUTH_ADMIN_SUPERVISEUR_GUICHETIER).contains("ADMIN", "SUPERVISEUR", "GUICHETIER");
        assertThat(SecurityConstants.AUTH_MANAGER).contains("MANAGER");
    }
}
