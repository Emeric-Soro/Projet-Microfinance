package com.microfinance.core_banking.api.controller.loan;

import com.microfinance.core_banking.entity.LoanFacility;
import com.microfinance.core_banking.service.extension.LoanFacilityService;
import com.microfinance.core_banking.support.AbstractControllerTest;
import com.microfinance.core_banking.support.ApiTestConstants;
import com.microfinance.core_banking.support.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.bean.MockBean;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoanFacilityController.class)
class LoanFacilityControllerTest extends AbstractControllerTest {

    @MockBean
    private LoanFacilityService loanFacilityService;

    private String adminToken;

    @BeforeEach
    void setUp() {
        adminToken = JwtTokenProvider.generateTokenWithRole("ADMIN");
    }

    @Test
    void shouldListerFacilities() throws Exception {
        LoanFacility facility = new LoanFacility();
        facility.setId(1L);
        facility.setCode("LF-001");
        facility.setMontantMax(new BigDecimal("5000000.00"));

        when(loanFacilityService.listerFacilities()).thenReturn(List.of(facility));

        mockMvc.perform(get(ApiTestConstants.LOAN_FACILITY_BASE)
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk());
    }

    @Test
    void shouldCreerFacility() throws Exception {
        LoanFacility facility = new LoanFacility();
        facility.setCode("LF-NEW");
        facility.setMontantMax(new BigDecimal("10000000.00"));
        when(loanFacilityService.creerFacility(any(LoanFacility.class))).thenReturn(facility);

        mockMvc.perform(post(ApiTestConstants.LOAN_FACILITY_BASE)
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(facility)))
            .andExpect(status().isCreated());
    }

    @Test
    void shouldGetById() throws Exception {
        LoanFacility facility = new LoanFacility();
        facility.setId(1L);
        when(loanFacilityService.rechercherParId(1L)).thenReturn(Optional.of(facility));

        mockMvc.perform(get(ApiTestConstants.LOAN_FACILITY_BASE + "/1")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk());
    }
}
