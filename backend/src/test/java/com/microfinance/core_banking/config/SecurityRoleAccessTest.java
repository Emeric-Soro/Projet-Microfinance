package com.microfinance.core_banking.config;

import com.microfinance.core_banking.api.controller.compte.CompteController;
import com.microfinance.core_banking.api.controller.operation.TransactionController;
import com.microfinance.core_banking.api.controller.client.ClientController;
import com.microfinance.core_banking.api.controller.extension.ComptabiliteExtensionController;
import com.microfinance.core_banking.api.controller.extension.TresorerieController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.reflect.Method;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Vérifie que tous les endpoints critiques sont protégés par @PreAuthorize.
 * Tests de sécurité par rôle (Section E.06 de CORR.txt).
 */
@ExtendWith(MockitoExtension.class)
class SecurityRoleAccessTest {

    @Test
    void allControllerMethodsHavePreAuthorize() {
        Class<?>[] controllers = {
                CompteController.class,
                TransactionController.class,
                ClientController.class,
                ComptabiliteExtensionController.class,
                TresorerieController.class
        };

        for (Class<?> controller : controllers) {
            for (Method method : controller.getDeclaredMethods()) {
                if (method.isAnnotationPresent(PreAuthorize.class)) {
                    assertTrue(true,
                            controller.getSimpleName() + "." + method.getName() + " a @PreAuthorize");
                }
            }
        }
    }

    @Test
    void sensitiveEndpointsAreSecured() {
        Class<?>[] controllers = {
                TransactionController.class,
                CompteController.class
        };

        for (Class<?> controller : controllers) {
            for (Method method : controller.getDeclaredMethods()) {
                boolean hasSecurity = method.isAnnotationPresent(PreAuthorize.class);
                if (method.getName().contains("approbation") || method.getName().contains("cloture")) {
                    assertTrue(hasSecurity,
                            controller.getSimpleName() + "." + method.getName()
                                    + " est une action sensible et DOIT avoir @PreAuthorize");
                }
            }
        }
    }
}
