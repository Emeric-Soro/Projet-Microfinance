package com.microfinance.core_banking.api.controller.extension;

import com.microfinance.core_banking.entity.PermissionSecurite;
import com.microfinance.core_banking.service.extension.PermissionSecuriteService;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PermissionSecuriteController.class)
class PermissionSecuriteControllerTest extends AbstractControllerTest {

    @MockBean
    private PermissionSecuriteService permissionService;

    private String adminToken;

    @BeforeEach
    void setUp() {
        adminToken = JwtTokenProvider.generateTokenWithRole("ADMIN");
    }

    @Test
    void shouldListerPermissions() throws Exception {
        PermissionSecurite perm = new PermissionSecurite();
        perm.setIdPermission(1L);
        perm.setCodePermission("CREDIT_MANAGE");
        perm.setActif(true);
        when(permissionService.listerPermissions()).thenReturn(List.of(perm));

        mockMvc.perform(get(ApiTestConstants.PERMISSIONS_BASE)
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].codePermission").value("CREDIT_MANAGE"));
    }

    @Test
    void shouldCreerPermission() throws Exception {
        PermissionSecurite perm = new PermissionSecurite();
        perm.setCodePermission("NEW_PERMISSION");
        perm.setDescription("Nouvelle permission");
        when(permissionService.creerPermission(any(PermissionSecurite.class))).thenReturn(perm);

        mockMvc.perform(post(ApiTestConstants.PERMISSIONS_BASE)
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(perm)))
            .andExpect(status().isCreated());
    }

    @Test
    void shouldGetPermissionById() throws Exception {
        PermissionSecurite perm = new PermissionSecurite();
        perm.setIdPermission(1L);
        perm.setCodePermission("CREDIT_MANAGE");
        when(permissionService.rechercherParId(1L)).thenReturn(Optional.of(perm));

        mockMvc.perform(get(ApiTestConstants.PERMISSIONS_BASE + "/1")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk());
    }

    @Test
    void shouldReturn401WithoutToken() throws Exception {
        mockMvc.perform(get(ApiTestConstants.PERMISSIONS_BASE)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }
}
