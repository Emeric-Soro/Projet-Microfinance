package com.microfinance.core_banking.api.controller.extension;

import com.microfinance.core_banking.entity.RoleUtilisateur;
import com.microfinance.core_banking.service.extension.RoleUtilisateurService;
import com.microfinance.core_banking.support.AbstractControllerTest;
import com.microfinance.core_banking.support.ApiTestConstants;
import com.microfinance.core_banking.support.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.bean.MockBean;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RoleUtilisateurController.class)
class RoleUtilisateurControllerTest extends AbstractControllerTest {

    @MockBean
    private RoleUtilisateurService roleService;

    private String adminToken;

    @BeforeEach
    void setUp() {
        adminToken = JwtTokenProvider.generateTokenWithRole("ADMIN");
    }

    @Test
    void shouldListerRoles() throws Exception {
        RoleUtilisateur role = new RoleUtilisateur();
        role.setIdRole(1L);
        role.setCodeRoleUtilisateur("ADMIN");
        role.setLibelleRole("Administrateur");
        when(roleService.listerRoles()).thenReturn(List.of(role));

        mockMvc.perform(get(ApiTestConstants.ROLES_BASE)
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].codeRoleUtilisateur").value("ADMIN"));
    }

    @Test
    void shouldCreerRole() throws Exception {
        RoleUtilisateur role = new RoleUtilisateur();
        role.setCodeRoleUtilisateur("MANAGER");
        role.setLibelleRole("Manager");
        when(roleService.creerRole(any(RoleUtilisateur.class))).thenReturn(role);

        mockMvc.perform(post(ApiTestConstants.ROLES_BASE)
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(role)))
            .andExpect(status().isCreated());
    }

    @Test
    void shouldAssignerPermissions() throws Exception {
        RoleUtilisateur role = new RoleUtilisateur();
        role.setIdRole(1L);
        role.setCodeRoleUtilisateur("MANAGER");
        when(roleService.assignerPermissions(eq(1L), any())).thenReturn(role);

        Map<String, Object> assignRequest = Map.of(
            "permissionIds", List.of(1L, 2L, 3L)
        );
        mockMvc.perform(put(ApiTestConstants.ROLES_BASE + "/1/permissions")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(assignRequest)))
            .andExpect(status().isOk());
    }

    @Test
    void shouldReturn401WithoutToken() throws Exception {
        mockMvc.perform(get(ApiTestConstants.ROLES_BASE)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }
}
