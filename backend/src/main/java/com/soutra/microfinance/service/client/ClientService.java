package com.soutra.microfinance.service.client;

import com.soutra.microfinance.dto.request.client.DecisionKycClientRequestDTO;
import com.soutra.microfinance.dto.request.client.MiseAJourKycClientRequestDTO;
import com.soutra.microfinance.entity.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClientService {

    Client creerClient(Client client);

    Client modifierStatutClient(Long idClient, String nouveauStatut);

    Client mettreAJourKyc(Long idClient, MiseAJourKycClientRequestDTO requestDTO);

    Client traiterDossierKyc(Long idClient, DecisionKycClientRequestDTO requestDTO);

    Client obtenirDetailsClient(Long idClient);

    Page<Client> listerClients(Pageable pageable);
}
