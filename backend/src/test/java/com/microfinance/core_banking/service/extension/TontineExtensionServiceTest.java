package com.microfinance.core_banking.service.extension;

import com.microfinance.core_banking.dto.request.extension.CreerTontineRequestDTO;
import com.microfinance.core_banking.entity.*;
import com.microfinance.core_banking.repository.extension.*;
import com.microfinance.core_banking.service.security.AuthenticatedUserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TontineExtensionServiceTest {

    @Mock private TontineRepository tontineRepository;
    @Mock private TourTontineRepository tourTontineRepository;
    @Mock private CotisationTontineRepository cotisationTontineRepository;
    @Mock private AuthenticatedUserService authenticatedUserService;
    @Mock private com.microfinance.core_banking.repository.client.ClientRepository clientRepository;
    @Mock private AgenceRepository agenceRepository;

    @InjectMocks
    private TontineExtensionService tontineExtensionService;

    @Test
    void creer_withValidData_shouldSucceed() {
        CreerTontineRequestDTO dto = new CreerTontineRequestDTO();
        dto.setCodeTontine("TNT-001");
        dto.setIntitule("Tontine Test");
        dto.setMontantCotisation(new BigDecimal("50000"));
        dto.setPeriodicite("MENSUEL");
        dto.setDateDebut(LocalDate.now());
        dto.setNombreParticipants(10);
        dto.setTypeTontine("STANDARD");
        dto.setIdAgence(1L);

        Agence agence = new Agence();
        agence.setIdAgence(1L);

        when(agenceRepository.findById(1L)).thenReturn(java.util.Optional.of(agence));
        when(tontineRepository.save(any(Tontine.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Tontine resultat = tontineExtensionService.creerTontine(dto);

        assertNotNull(resultat);
        assertEquals("Tontine Test", resultat.getIntitule());
        assertEquals(0, new BigDecimal("50000").compareTo(resultat.getMontantCotisation()));
    }
}
