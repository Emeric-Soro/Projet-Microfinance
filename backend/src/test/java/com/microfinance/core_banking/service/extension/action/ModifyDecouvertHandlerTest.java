package com.microfinance.core_banking.service.extension.action;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.entity.Compte;
import com.microfinance.core_banking.service.compte.CompteService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ModifyDecouvertHandlerTest {

    @Mock
    private CompteService compteService;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private ModifyDecouvertHandler handler;

    @Test
    void execute_shouldCallChangerDecouvertAutorise() {
        ActionEnAttente action = new ActionEnAttente();
        Map<String, Object> payload = Map.of(
            "numCompte", "FR76123456789",
            "nouveauPlafond", 5000
        );

        Compte compte = new Compte();
        compte.setNumCompte("FR76123456789");

        when(compteService.changerDecouvertAutorise("FR76123456789", BigDecimal.valueOf(5000)))
            .thenReturn(compte);

        String result = handler.execute(action, payload);

        assertEquals("FR76123456789", result);
        verify(compteService).changerDecouvertAutorise("FR76123456789", BigDecimal.valueOf(5000));
    }

}
