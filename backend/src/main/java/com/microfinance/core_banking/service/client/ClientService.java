package com.microfinance.core_banking.service.client;

import com.microfinance.core_banking.entity.Client;

public interface ClientService {

    Client creerClient(Client client);

    Client modifierStatutClient(Long idClient, String nouveauStatut);

    Client obtenirDetailsClient(Long idClient);
}
