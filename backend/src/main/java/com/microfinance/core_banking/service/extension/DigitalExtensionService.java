package com.microfinance.core_banking.service.extension;

import com.microfinance.core_banking.dto.request.extension.CreerEmployeServiceRequestDTO;
import com.microfinance.core_banking.dto.request.extension.CreerPartenaireServiceRequestDTO;
import com.microfinance.core_banking.dto.request.extension.EnregistrerAppareilServiceRequestDTO;
import com.microfinance.core_banking.entity.AppareilClient;
import com.microfinance.core_banking.entity.Client;
import com.microfinance.core_banking.entity.Employe;
import com.microfinance.core_banking.entity.PartenaireApi;
import com.microfinance.core_banking.repository.client.ClientRepository;
import com.microfinance.core_banking.repository.extension.AgenceRepository;
import com.microfinance.core_banking.repository.extension.AppareilClientRepository;
import com.microfinance.core_banking.repository.extension.EmployeRepository;
import com.microfinance.core_banking.repository.extension.PartenaireApiRepository;
import com.microfinance.core_banking.service.security.AuthenticatedUserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DigitalExtensionService {

    private final AppareilClientRepository appareilClientRepository;
    private final PartenaireApiRepository partenaireApiRepository;
    private final EmployeRepository employeRepository;
    private final ClientRepository clientRepository;
    private final AgenceRepository agenceRepository;
    private final AuthenticatedUserService authenticatedUserService;

    public DigitalExtensionService(
            AppareilClientRepository appareilClientRepository,
            PartenaireApiRepository partenaireApiRepository,
            EmployeRepository employeRepository,
            ClientRepository clientRepository,
            AgenceRepository agenceRepository,
            AuthenticatedUserService authenticatedUserService
    ) {
        this.appareilClientRepository = appareilClientRepository;
        this.partenaireApiRepository = partenaireApiRepository;
        this.employeRepository = employeRepository;
        this.clientRepository = clientRepository;
        this.agenceRepository = agenceRepository;
        this.authenticatedUserService = authenticatedUserService;
    }

    @Transactional
    public AppareilClient enregistrerAppareil(EnregistrerAppareilServiceRequestDTO dto) {
        Client client = clientRepository.findById(Long.valueOf(dto.getIdClient()))
                .orElseThrow(() -> new EntityNotFoundException("Client introuvable"));
        if (client.getAgence() != null) {
            authenticatedUserService.assertAgencyAccess(client.getAgence().getIdAgence());
        }
        AppareilClient appareil = new AppareilClient();
        appareil.setClient(client);
        appareil.setEmpreinteAppareil(dto.getEmpreinteAppareil());
        appareil.setPlateforme(dto.getPlateforme());
        appareil.setNomAppareil(dto.getNomAppareil());
        appareil.setAutorise(dto.getAutorise());
        appareil.setDerniereConnexion(LocalDateTime.now());
        return appareilClientRepository.save(appareil);
    }

    @Transactional
    public PartenaireApi creerPartenaire(CreerPartenaireServiceRequestDTO dto) {
        PartenaireApi partenaire = new PartenaireApi();
        partenaire.setCodePartenaire(dto.getCodePartenaire());
        partenaire.setNomPartenaire(dto.getNomPartenaire());
        partenaire.setTypePartenaire(dto.getTypePartenaire());
        partenaire.setWebhookUrl(dto.getWebhookUrl());
        partenaire.setStatut(dto.getStatut());
        partenaire.setOauthClientId(dto.getOauthClientId());
        partenaire.setCleApi(dto.getCleApi());
        partenaire.setQuotasJournaliers(dto.getQuotasJournaliers());
        return partenaireApiRepository.save(partenaire);
    }

    @Transactional
    public Employe creerEmploye(CreerEmployeServiceRequestDTO dto) {
        Employe employe = new Employe();
        employe.setMatricule(dto.getMatricule());
        employe.setNomComplet(dto.getNomComplet());
        employe.setPoste(dto.getPoste());
        employe.setService(dto.getService());
        employe.setStatut(dto.getStatut());
        employe.setDateEmbauche(dto.getDateEmbauche() == null ? LocalDate.now() : LocalDate.parse(dto.getDateEmbauche()));
        employe.setEmail(dto.getEmail());
        employe.setTelephone(dto.getTelephone());
        if (dto.getIdAgence() != null) {
            var agence = agenceRepository.findById(Long.valueOf(dto.getIdAgence()))
                    .orElseThrow(() -> new EntityNotFoundException("Agence introuvable"));
            authenticatedUserService.assertAgencyAccess(agence.getIdAgence());
            employe.setAgence(agence);
        }
        return employeRepository.save(employe);
    }

    @Transactional(readOnly = true)
    public List<AppareilClient> listerAppareils() {
        return appareilClientRepository.findAll().stream()
                .filter(appareil -> authenticatedUserService.hasGlobalScope()
                        || (authenticatedUserService.getCurrentAgencyId() != null
                        && appareil.getClient() != null
                        && appareil.getClient().getAgence() != null
                        && authenticatedUserService.getCurrentAgencyId().equals(appareil.getClient().getAgence().getIdAgence())))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PartenaireApi> listerPartenaires() {
        return partenaireApiRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Employe> listerEmployes() {
        return employeRepository.findAll().stream()
                .filter(employe -> authenticatedUserService.hasGlobalScope()
                        || (authenticatedUserService.getCurrentAgencyId() != null
                        && employe.getAgence() != null
                        && authenticatedUserService.getCurrentAgencyId().equals(employe.getAgence().getIdAgence())))
                .toList();
    }
}
