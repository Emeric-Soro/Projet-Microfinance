package com.microfinance.core_banking.service.extension;

import com.microfinance.core_banking.dto.request.extension.EnregistrerAppareilServiceRequestDTO;
import com.microfinance.core_banking.entity.*;
import com.microfinance.core_banking.repository.client.ClientRepository;
import com.microfinance.core_banking.repository.extension.*;
import com.microfinance.core_banking.service.security.AuthenticatedUserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DigitalExtensionServiceTest {

    @Mock private AppareilClientRepository appareilClientRepository;
    @Mock private PartenaireApiRepository partenaireApiRepository;
    @Mock private EmployeRepository employeRepository;
    @Mock private ClientRepository clientRepository;
    @Mock private AuthenticatedUserService authenticatedUserService;

    @InjectMocks
    private DigitalExtensionService digitalExtensionService;

    @Test
    void enregistrerAppareil_withValidData_shouldSucceed() {
        EnregistrerAppareilServiceRequestDTO dto = new EnregistrerAppareilServiceRequestDTO();
        dto.setIdClient("1");
        dto.setPlateforme("MOBILE");
        dto.setEmpreinteAppareil("IMEI-123456789");

        Client client = new Client();
        client.setIdClient(1L);

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(appareilClientRepository.save(any(AppareilClient.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AppareilClient resultat = digitalExtensionService.enregistrerAppareil(dto);

        assertNotNull(resultat);
        assertEquals("MOBILE", resultat.getPlateforme());
        assertEquals("IMEI-123456789", resultat.getEmpreinteAppareil());
    }

    @Test
    void enregistrerAppareil_withUnknownClient_shouldThrow() {
        EnregistrerAppareilServiceRequestDTO dto = new EnregistrerAppareilServiceRequestDTO();
        dto.setIdClient("999");
        dto.setPlateforme("MOBILE");
        dto.setEmpreinteAppareil("IMEI-999");

        when(clientRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(jakarta.persistence.EntityNotFoundException.class,
                () -> digitalExtensionService.enregistrerAppareil(dto));
    }
}
