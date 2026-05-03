package com.microfinance.core_banking.config;

import com.microfinance.core_banking.entity.Client;
import com.microfinance.core_banking.entity.RoleUtilisateur;
import com.microfinance.core_banking.entity.StatutClient;
import com.microfinance.core_banking.entity.Utilisateur;
import com.microfinance.core_banking.repository.client.ClientRepository;
import com.microfinance.core_banking.repository.client.RoleUtilisateurRepository;
import com.microfinance.core_banking.repository.client.StatutClientRepository;
import com.microfinance.core_banking.repository.client.UtilisateurRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DemoUserBootstrapTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private StatutClientRepository statutClientRepository;

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Mock
    private RoleUtilisateurRepository roleUtilisateurRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthSecurityProperties authSecurityProperties;

    private DemoUserProperties demoUserProperties;
    private DemoUserBootstrap demoUserBootstrap;

    @BeforeEach
    void setUp() {
        demoUserProperties = new DemoUserProperties();
        demoUserProperties.setEnabled(true);

        demoUserBootstrap = new DemoUserBootstrap(
                demoUserProperties,
                authSecurityProperties,
                clientRepository,
                statutClientRepository,
                utilisateurRepository,
                roleUtilisateurRepository,
                passwordEncoder
        );
    }

    @Test
    void shouldCreateDemoClientAndUserWhenBootstrapRuns() throws Exception {
        when(authSecurityProperties.getCredentialsValidityDays()).thenReturn(90);
        when(passwordEncoder.encode("Demo@12345")).thenReturn("hashed-demo-password");

        when(statutClientRepository.findByLibelleStatutIgnoreCase("ACTIF")).thenReturn(Optional.empty());
        when(statutClientRepository.save(any(StatutClient.class))).thenAnswer(invocation -> {
            StatutClient statutClient = invocation.getArgument(0);
            statutClient.setIdStatutClient(1L);
            return statutClient;
        });

        when(roleUtilisateurRepository.findByCodeRoleUtilisateur("ADMIN")).thenReturn(Optional.empty());
        when(roleUtilisateurRepository.save(any(RoleUtilisateur.class))).thenAnswer(invocation -> {
            RoleUtilisateur roleUtilisateur = invocation.getArgument(0);
            roleUtilisateur.setIdRole(2L);
            return roleUtilisateur;
        });

        when(utilisateurRepository.findByLogin("demo.admin")).thenReturn(Optional.empty());
        when(clientRepository.findByCodeClient("CLI-DEMO-0001")).thenReturn(Optional.empty());
        when(clientRepository.findByEmail("demo.admin@microfin.local")).thenReturn(Optional.empty());
        when(clientRepository.findByTelephone("700000001")).thenReturn(Optional.empty());

        when(clientRepository.save(any(Client.class))).thenAnswer(invocation -> {
            Client client = invocation.getArgument(0);
            client.setIdClient(42L);
            return client;
        });

        when(utilisateurRepository.findByClient_IdClient(42L)).thenReturn(Optional.empty());
        when(utilisateurRepository.save(any(Utilisateur.class))).thenAnswer(invocation -> {
            Utilisateur utilisateur = invocation.getArgument(0);
            utilisateur.setIdUser(99L);
            return utilisateur;
        });

        demoUserBootstrap.run(null);

        ArgumentCaptor<Client> clientCaptor = ArgumentCaptor.forClass(Client.class);
        verify(clientRepository).save(clientCaptor.capture());
        Client client = clientCaptor.getValue();
        assertThat(client.getCodeClient()).isEqualTo("CLI-DEMO-0001");
        assertThat(client.getEmail()).isEqualTo("demo.admin@microfin.local");
        assertThat(client.getTelephone()).isEqualTo("700000001");
        assertThat(client.getStatutClient()).isNotNull();
        assertThat(client.getStatutClient().getLibelleStatut()).isEqualTo("ACTIF");

        ArgumentCaptor<Utilisateur> utilisateurCaptor = ArgumentCaptor.forClass(Utilisateur.class);
        verify(utilisateurRepository).save(utilisateurCaptor.capture());
        Utilisateur utilisateur = utilisateurCaptor.getValue();
        assertThat(utilisateur.getLogin()).isEqualTo("demo.admin");
        assertThat(utilisateur.getPassword()).isEqualTo("hashed-demo-password");
        assertThat(utilisateur.getActif()).isTrue();
        assertThat(utilisateur.getSecondFacteurActive()).isFalse();
        assertThat(utilisateur.getRoles())
                .extracting(RoleUtilisateur::getCodeRoleUtilisateur)
                .contains("ADMIN");
        assertThat(utilisateur.getClient()).isSameAs(client);
    }
}
