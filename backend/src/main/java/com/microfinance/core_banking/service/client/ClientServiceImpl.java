package com.microfinance.core_banking.service.client;

import com.microfinance.core_banking.dto.request.client.DecisionKycClientRequestDTO;
import com.microfinance.core_banking.dto.request.client.MiseAJourKycClientRequestDTO;
import com.microfinance.core_banking.entity.Client;
import com.microfinance.core_banking.entity.NiveauRisqueClient;
import com.microfinance.core_banking.entity.StatutClient;
import com.microfinance.core_banking.entity.StatutKycClient;
import com.microfinance.core_banking.repository.client.ClientRepository;
import com.microfinance.core_banking.repository.client.StatutClientRepository;
import com.microfinance.core_banking.service.extension.AmlService;
import com.microfinance.core_banking.service.extension.ConformiteExtensionService;
import com.microfinance.core_banking.service.security.AuthenticatedUserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final StatutClientRepository statutClientRepository;
    private final AuthenticatedUserService authenticatedUserService;
    private final ConformiteExtensionService conformiteExtensionService;
    private final AmlService amlService;

    public ClientServiceImpl(
            ClientRepository clientRepository,
            StatutClientRepository statutClientRepository,
            AuthenticatedUserService authenticatedUserService,
            ConformiteExtensionService conformiteExtensionService,
            AmlService amlService
    ) {
        this.clientRepository = clientRepository;
        this.statutClientRepository = statutClientRepository;
        this.authenticatedUserService = authenticatedUserService;
        this.conformiteExtensionService = conformiteExtensionService;
        this.amlService = amlService;
    }

    @Override
    @Transactional
    public Client creerClient(Client client) {
        if (client == null) {
            throw new IllegalArgumentException("Le client ne peut pas etre null");
        }
        if (client.getEmail() != null && clientRepository.existsByEmail(client.getEmail())) {
            throw new IllegalArgumentException("Email deja utilise");
        }
        if (client.getTelephone() != null && clientRepository.existsByTelephone(client.getTelephone())) {
            throw new IllegalArgumentException("Telephone deja utilise");
        }
        validerUnicitePiece(client.getNumeroPieceIdentite(), null);

        client.setCodeClient(genererCodeClientUnique());
        client.setDateInscription(LocalDate.now());
        client.setPep(Boolean.TRUE.equals(client.getPep()));
        client.setNiveauRisque(NiveauRisqueClient.FAIBLE);
        initialiserWorkflowKyc(client);
        if (client.getAgence() == null) {
            client.setAgence(authenticatedUserService.getCurrentUserOptional()
                    .map(user -> user.getAgenceActive())
                    .orElse(null));
        }

        StatutClient statutParDefaut = statutClientRepository.findByLibelleStatutIgnoreCase("NOUVEAU")
                .orElseThrow(() -> new IllegalStateException("Erreur critique : Le statut 'NOUVEAU' n'est pas paramétré en base."));

        client.setStatutClient(statutParDefaut);
        Client clientSauvegarde = clientRepository.save(client);
        // Lancer le screening AML automatique apres creation
        amlService.screenerClient(clientSauvegarde);
        return clientSauvegarde;
    }

    @Override
    @Transactional
    public Client modifierStatutClient(Long idClient, String nouveauStatut) {
        Client client = clientRepository.findById(idClient)
                .orElseThrow(() -> new EntityNotFoundException("Client introuvable: " + idClient));

        if (nouveauStatut == null || nouveauStatut.isBlank()) {
            throw new IllegalArgumentException("Le nouveau statut est obligatoire");
        }

        StatutClient statut = statutClientRepository.findByLibelleStatutIgnoreCase(nouveauStatut)
                .orElseThrow(() -> new IllegalArgumentException("Le statut '" + nouveauStatut + "' n'existe pas dans le système."));

        client.setStatutClient(statut);
        return clientRepository.save(client);
    }

    @Override
    @Transactional
    public Client mettreAJourKyc(Long idClient, MiseAJourKycClientRequestDTO requestDTO) {
        Client client = clientRepository.findById(idClient)
                .orElseThrow(() -> new EntityNotFoundException("Client introuvable: " + idClient));

        validerUnicitePiece(requestDTO.getNumeroPieceIdentite(), client.getIdClient());
        appliquerDonneesKyc(client, requestDTO);

        client.setStatutKyc(StatutKycClient.EN_ATTENTE);
        client.setDateSoumissionKyc(requestDTO.getDateSoumission() == null ? LocalDate.now() : requestDTO.getDateSoumission());
        client.setDateValidationKyc(null);
        client.setCommentaireKyc(null);
        client.setValidateurKyc(null);
        return clientRepository.save(client);
    }

    @Override
    @Transactional
    public Client traiterDossierKyc(Long idClient, DecisionKycClientRequestDTO requestDTO) {
        Client client = clientRepository.findById(idClient)
                .orElseThrow(() -> new EntityNotFoundException("Client introuvable: " + idClient));

        if (requestDTO.getStatutKyc() == StatutKycClient.BROUILLON || requestDTO.getStatutKyc() == StatutKycClient.EN_ATTENTE) {
            throw new IllegalArgumentException("La decision KYC doit etre VALIDE, A_REVOIR ou REJETE");
        }
        if (requestDTO.getStatutKyc() == StatutKycClient.VALIDE && !dossierKycComplet(client)) {
            throw new IllegalStateException("Le dossier KYC est incomplet et ne peut pas etre valide");
        }

        client.setStatutKyc(requestDTO.getStatutKyc());
        client.setNiveauRisque(requestDTO.getNiveauRisque());
        client.setCommentaireKyc(requestDTO.getCommentaire());
        client.setValidateurKyc(requestDTO.getValidateurKyc().trim());
        client.setDateValidationKyc(LocalDate.now());

        if (requestDTO.getStatutKyc() == StatutKycClient.VALIDE) {
            client.setStatutClient(chargerStatutStrict("ACTIF"));
        } else if (requestDTO.getStatutKyc() == StatutKycClient.REJETE) {
            client.setStatutClient(chargerStatutStrict("BLOQUE"));
        }

        Client saved = clientRepository.save(client);
        conformiteExtensionService.analyserClient(saved, "KYC_REVIEW");
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public Client obtenirDetailsClient(Long idClient) {
        Client client = clientRepository.findById(idClient)
                .orElseThrow(() -> new EntityNotFoundException("Client introuvable: " + idClient));
        verifierPerimetreAgence(client);
        return client;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Client> listerClients(Pageable pageable) {
        if (!authenticatedUserService.hasGlobalScope()) {
            Long idAgence = authenticatedUserService.getCurrentAgencyId();
            if (idAgence == null) {
                return Page.empty(pageable);
            }
            return clientRepository.findByAgence_IdAgence(idAgence, pageable);
        }
        return clientRepository.findAll(pageable);
    }

    private void verifierPerimetreAgence(Client client) {
        if (client == null || client.getAgence() == null) {
            return;
        }
        authenticatedUserService.assertAgencyAccess(client.getAgence().getIdAgence());
    }

    private String genererCodeClientUnique() {
        String prefixeDate = LocalDate.now().toString().replace("-", "");
        for (int tentative = 0; tentative < 20; tentative++) {
            int suffixe = ThreadLocalRandom.current().nextInt(1000, 10000);
            String code = "CLI-" + prefixeDate + "-" + suffixe;
            if (!clientRepository.existsByCodeClient(code)) {
                return code;
            }
        }
        throw new IllegalStateException("Impossible de generer un code client unique");
    }

    private void initialiserWorkflowKyc(Client client) {
        if (dossierKycComplet(client)) {
            if (client.getDateExpirationPieceIdentite() != null && client.getDateExpirationPieceIdentite().isBefore(LocalDate.now())) {
                throw new IllegalArgumentException("La piece d'identite du client est expiree");
            }
            client.setStatutKyc(StatutKycClient.EN_ATTENTE);
            client.setDateSoumissionKyc(LocalDate.now());
        } else {
            client.setStatutKyc(StatutKycClient.BROUILLON);
            client.setDateSoumissionKyc(null);
        }
        client.setDateValidationKyc(null);
        client.setCommentaireKyc(null);
        client.setValidateurKyc(null);
    }

    private void appliquerDonneesKyc(Client client, MiseAJourKycClientRequestDTO requestDTO) {
        if (requestDTO.getDateExpirationPieceIdentite().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La piece d'identite ne peut pas etre expiree");
        }

        client.setProfession(requestDTO.getProfession().trim());
        client.setEmployeur(requestDTO.getEmployeur());
        client.setTypePieceIdentite(requestDTO.getTypePieceIdentite());
        client.setNumeroPieceIdentite(normaliserNumeroPiece(requestDTO.getNumeroPieceIdentite()));
        client.setDateExpirationPieceIdentite(requestDTO.getDateExpirationPieceIdentite());
        client.setPhotoIdentiteUrl(requestDTO.getPhotoIdentiteUrl().trim());
        client.setJustificatifDomicileUrl(requestDTO.getJustificatifDomicileUrl().trim());
        client.setJustificatifRevenusUrl(requestDTO.getJustificatifRevenusUrl().trim());
        client.setPaysNationalite(requestDTO.getPaysNationalite().trim());
        client.setPaysResidence(requestDTO.getPaysResidence().trim());
        client.setPep(Boolean.TRUE.equals(requestDTO.getPep()));
    }

    private boolean dossierKycComplet(Client client) {
        return client.getProfession() != null
                && !client.getProfession().isBlank()
                && client.getTypePieceIdentite() != null
                && client.getNumeroPieceIdentite() != null
                && !client.getNumeroPieceIdentite().isBlank()
                && client.getDateExpirationPieceIdentite() != null
                && !client.getDateExpirationPieceIdentite().isBefore(LocalDate.now())
                && client.getPhotoIdentiteUrl() != null
                && !client.getPhotoIdentiteUrl().isBlank()
                && client.getJustificatifDomicileUrl() != null
                && !client.getJustificatifDomicileUrl().isBlank()
                && client.getJustificatifRevenusUrl() != null
                && !client.getJustificatifRevenusUrl().isBlank()
                && client.getPaysNationalite() != null
                && !client.getPaysNationalite().isBlank()
                && client.getPaysResidence() != null
                && !client.getPaysResidence().isBlank();
    }

    private void validerUnicitePiece(String numeroPieceIdentite, Long idClientCourant) {
        if (numeroPieceIdentite == null || numeroPieceIdentite.isBlank()) {
            return;
        }

        String numeroNormalise = normaliserNumeroPiece(numeroPieceIdentite);
        boolean existe = clientRepository.existsByNumeroPieceIdentite(numeroNormalise);
        if (!existe) {
            return;
        }

        if (idClientCourant != null) {
            Client client = clientRepository.findById(idClientCourant)
                    .orElseThrow(() -> new EntityNotFoundException("Client introuvable: " + idClientCourant));
            if (numeroNormalise.equals(client.getNumeroPieceIdentite())) {
                return;
            }
        }

        throw new IllegalArgumentException("Numero de piece d'identite deja utilise");
    }

    private String normaliserNumeroPiece(String numeroPieceIdentite) {
        return numeroPieceIdentite == null ? null : numeroPieceIdentite.trim().toUpperCase();
    }

    private StatutClient chargerStatutStrict(String libelleStatut) {
        return statutClientRepository.findByLibelleStatutIgnoreCase(libelleStatut)
                .orElseThrow(() -> new IllegalStateException("Erreur critique : Le statut '" + libelleStatut + "' n'est pas paramétré en base."));
    }
}
