package com.microfinance.core_banking.config;

import com.microfinance.core_banking.entity.Client;
import com.microfinance.core_banking.entity.Utilisateur;
import com.microfinance.core_banking.repository.compte.CompteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountAccessSecurityTest {

    @Mock
    private CompteRepository compteRepository;

    @InjectMocks
    private AccountAccessSecurity accountAccessSecurity;

    @Test
    void shouldAllowClientAccessToOwnAccount() {
        Utilisateur utilisateur = buildUtilisateur(9L);
        Authentication authentication = new UsernamePasswordAuthenticationToken(utilisateur, null, utilisateur.getAuthorities());

        when(compteRepository.existsByNumCompteAndClient_IdClient("CPT-001", 9L)).thenReturn(true);

        assertThat(accountAccessSecurity.canAccessAccount(authentication, "CPT-001")).isTrue();
    }

    @Test
    void shouldDenyClientAccessToAnotherAccount() {
        Utilisateur utilisateur = buildUtilisateur(9L);
        Authentication authentication = new UsernamePasswordAuthenticationToken(utilisateur, null, utilisateur.getAuthorities());

        when(compteRepository.existsByNumCompteAndClient_IdClient("CPT-002", 9L)).thenReturn(false);

        assertThat(accountAccessSecurity.canAccessAccount(authentication, "CPT-002")).isFalse();
    }

    private Utilisateur buildUtilisateur(Long idClient) {
        Client client = new Client();
        client.setIdClient(idClient);

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setClient(client);
        return utilisateur;
    }
}
