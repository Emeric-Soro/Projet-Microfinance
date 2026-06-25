package com.soutra.microfinance.service.client;

import com.soutra.microfinance.dto.request.client.DecisionKycClientRequestDTO;
import com.soutra.microfinance.dto.request.client.MiseAJourKycClientRequestDTO;
import com.soutra.microfinance.dto.request.client.MiseAJourClientRequestDTO;
import com.soutra.microfinance.entity.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClientService {

    Client creerClient(Client client);

    Client modifierStatutClient(Long idClient, String nouveauStatut);

    Client mettreAJourKyc(Long idClient, MiseAJourKycClientRequestDTO requestDTO);

    Client traiterDossierKyc(Long idClient, DecisionKycClientRequestDTO requestDTO);

    Client obtenirDetailsClient(Long idClient);

    Client modifierProfilMobile(Long idClient, String telephone, String email, String adresse);

    Client modifierProfilClient(Long idClient, MiseAJourClientRequestDTO requestDTO);

    Client mettreAJourKycMobile(Long idClient, String profession, String secteurActivite, java.math.BigDecimal revenuMensuel);

    Client enregistrerDocumentKycMobile(Long idClient, String typeDocument, String nomFichier, String contenuBase64);

    Page<Client> listerClients(Pageable pageable);

    Page<Client> rechercherClients(String query, Pageable pageable);
}
