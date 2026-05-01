package com.microfinance.core_banking.service.extension;

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
import java.util.Map;

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
    public AppareilClient enregistrerAppareil(Map<String, Object> payload) {
        Client client = clientRepository.findById(Long.valueOf(required(payload, "idClient")))
                .orElseThrow(() -> new EntityNotFoundException("Client introuvable"));
        if (client.getAgence() != null) {
            authenticatedUserService.assertAgencyAccess(client.getAgence().getIdAgence());
        }
        AppareilClient appareil = new AppareilClient();
        appareil.setClient(client);
        appareil.setEmpreinteAppareil(required(payload, "empreinteAppareil"));
        appareil.setPlateforme(required(payload, "plateforme"));
        appareil.setNomAppareil((String) payload.get("nomAppareil"));
        appareil.setAutorise(payload.get("autorise") == null || Boolean.parseBoolean(payload.get("autorise").toString()));
        appareil.setDerniereConnexion(LocalDateTime.now());
        return appareilClientRepository.save(appareil);
    }

    @Transactional
    public PartenaireApi creerPartenaire(Map<String, Object> payload) {
        PartenaireApi partenaire = new PartenaireApi();
        partenaire.setCodePartenaire(required(payload, "codePartenaire"));
        partenaire.setNomPartenaire(required(payload, "nomPartenaire"));
        partenaire.setTypePartenaire(required(payload, "typePartenaire"));
        partenaire.setWebhookUrl((String) payload.get("webhookUrl"));
        partenaire.setStatut(defaulted(payload, "statut", "ACTIF"));
        partenaire.setOauthClientId((String) payload.get("oauthClientId"));
        partenaire.setCleApi((String) payload.get("cleApi"));
        partenaire.setQuotasJournaliers(payload.get("quotasJournaliers") == null ? 0 : Integer.parseInt(payload.get("quotasJournaliers").toString()));
        return partenaireApiRepository.save(partenaire);
    }

    @Transactional
    public Employe creerEmploye(Map<String, Object> payload) {
        Employe employe = new Employe();
        employe.setMatricule(required(payload, "matricule"));
        employe.setNomComplet(required(payload, "nomComplet"));
        employe.setPoste((String) payload.get("poste"));
        employe.setService((String) payload.get("service"));
        employe.setStatut(defaulted(payload, "statut", "ACTIF"));
        employe.setDateEmbauche(payload.get("dateEmbauche") == null ? LocalDate.now() : LocalDate.parse(payload.get("dateEmbauche").toString()));
        employe.setEmail((String) payload.get("email"));
        employe.setTelephone((String) payload.get("telephone"));
        if (payload.get("idAgence") != null) {
            var agence = agenceRepository.findById(Long.valueOf(payload.get("idAgence").toString()))
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

    private String required(Map<String, Object> payload, String key) {
        Object value = payload.get(key);
        if (value == null || value.toString().isBlank()) {
            throw new IllegalArgumentException("Le champ '" + key + "' est obligatoire");
        }
        return value.toString().trim();
    }

    private String defaulted(Map<String, Object> payload, String key, String defaultValue) {
        Object value = payload.get(key);
        return value == null || value.toString().isBlank() ? defaultValue : value.toString().trim();
    }
}
