package com.microfinance.core_banking.service.client;

import com.microfinance.core_banking.entity.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClientService {

    Client creerClient(Client client);

    Client modifierStatutClient(Long idClient, String nouveauStatut);

    Client obtenirDetailsClient(Long idClient);

    Page<Client> listerClients(Pageable pageable);
}
