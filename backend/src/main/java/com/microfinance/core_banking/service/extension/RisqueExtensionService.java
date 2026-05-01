package com.microfinance.core_banking.service.extension;

import com.microfinance.core_banking.entity.Caisse;
import com.microfinance.core_banking.entity.Client;
import com.microfinance.core_banking.entity.Coffre;
import com.microfinance.core_banking.entity.Credit;
import com.microfinance.core_banking.entity.IncidentOperationnel;
import com.microfinance.core_banking.entity.ResultatStressTest;
import com.microfinance.core_banking.entity.Risque;
import com.microfinance.core_banking.entity.StressTest;
import com.microfinance.core_banking.repository.extension.CaisseRepository;
import com.microfinance.core_banking.repository.extension.CoffreRepository;
import com.microfinance.core_banking.repository.extension.CreditRepository;
import com.microfinance.core_banking.repository.extension.IncidentOperationnelRepository;
import com.microfinance.core_banking.repository.extension.ResultatStressTestRepository;
import com.microfinance.core_banking.repository.extension.RisqueRepository;
import com.microfinance.core_banking.repository.extension.StressTestRepository;
import com.microfinance.core_banking.repository.compte.CompteRepository;
import com.microfinance.core_banking.service.security.AuthenticatedUserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class RisqueExtensionService {

    private final RisqueRepository risqueRepository;
    private final IncidentOperationnelRepository incidentOperationnelRepository;
    private final StressTestRepository stressTestRepository;
    private final ResultatStressTestRepository resultatStressTestRepository;
    private final CreditRepository creditRepository;
    private final CompteRepository compteRepository;
    private final CaisseRepository caisseRepository;
    private final CoffreRepository coffreRepository;
    private final AuthenticatedUserService authenticatedUserService;

    public RisqueExtensionService(
            RisqueRepository risqueRepository,
            IncidentOperationnelRepository incidentOperationnelRepository,
            StressTestRepository stressTestRepository,
            ResultatStressTestRepository resultatStressTestRepository,
            CreditRepository creditRepository,
            CompteRepository compteRepository,
            CaisseRepository caisseRepository,
            CoffreRepository coffreRepository,
            AuthenticatedUserService authenticatedUserService
    ) {
        this.risqueRepository = risqueRepository;
        this.incidentOperationnelRepository = incidentOperationnelRepository;
        this.stressTestRepository = stressTestRepository;
        this.resultatStressTestRepository = resultatStressTestRepository;
        this.creditRepository = creditRepository;
        this.compteRepository = compteRepository;
        this.caisseRepository = caisseRepository;
        this.coffreRepository = coffreRepository;
        this.authenticatedUserService = authenticatedUserService;
    }

    @Transactional
    public Risque creerRisque(Map<String, Object> payload) {
        Risque risque = new Risque();
        risque.setCodeRisque(defaulted(payload, "codeRisque", "RSK-" + randomSuffix()));
        risque.setCategorie(required(payload, "categorie"));
        risque.setLibelle(required(payload, "libelle"));
        risque.setNiveau(required(payload, "niveau"));
        risque.setStatut(defaulted(payload, "statut", "OUVERT"));
        return risqueRepository.save(risque);
    }

    @Transactional
    public IncidentOperationnel declarerIncident(Map<String, Object> payload) {
        IncidentOperationnel incident = new IncidentOperationnel();
        incident.setReferenceIncident(defaulted(payload, "referenceIncident", "INC-" + randomSuffix()));
        incident.setTypeIncident(required(payload, "typeIncident"));
        incident.setGravite(required(payload, "gravite"));
        incident.setStatut(defaulted(payload, "statut", "OUVERT"));
        incident.setDescription((String) payload.get("description"));
        if (payload.get("idRisque") != null) {
            Risque risque = risqueRepository.findById(Long.valueOf(payload.get("idRisque").toString()))
                    .orElseThrow(() -> new EntityNotFoundException("Risque introuvable"));
            incident.setRisque(risque);
        }
        return incidentOperationnelRepository.save(incident);
    }

    @Transactional
    public StressTest creerStressTest(Map<String, Object> payload) {
        StressTest stressTest = new StressTest();
        stressTest.setCodeScenario(defaulted(payload, "codeScenario", "ST-" + randomSuffix()));
        stressTest.setLibelle(required(payload, "libelle"));
        stressTest.setTauxDefaut(decimalOrZero(payload, "tauxDefaut"));
        stressTest.setTauxRetrait(decimalOrZero(payload, "tauxRetrait"));
        stressTest.setStatut(defaulted(payload, "statut", "ACTIF"));
        return stressTestRepository.save(stressTest);
    }

    @Transactional
    public ResultatStressTest executerStressTest(Long idStressTest) {
        StressTest stressTest = stressTestRepository.findById(idStressTest)
                .orElseThrow(() -> new EntityNotFoundException("Stress test introuvable"));

        BigDecimal encoursCredit = creditsVisibles().stream()
                .map(Credit::getCapitalRestantDu)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal baseDepots = comptesVisibles().stream()
                .map(compte -> compte.getSolde().max(BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal retraitsProjetes = baseDepots.multiply(stressTest.getTauxRetrait()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal pertesProjetees = encoursCredit.multiply(stressTest.getTauxDefaut()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal liquiditeDisponible = caissesVisibles().stream()
                .map(Caisse::getSoldeTheorique)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .add(coffresVisibles().stream().map(Coffre::getSoldeTheorique).reduce(BigDecimal.ZERO, BigDecimal::add));

        ResultatStressTest resultat = new ResultatStressTest();
        resultat.setStressTest(stressTest);
        resultat.setEncoursCredit(encoursCredit);
        resultat.setPertesProjetees(pertesProjetees);
        resultat.setRetraitsProjetes(retraitsProjetes);
        resultat.setLiquiditeNette(liquiditeDisponible.subtract(retraitsProjetes).subtract(pertesProjetees));
        resultat.setStatutResultat(resultat.getLiquiditeNette().compareTo(BigDecimal.ZERO) >= 0 ? "STABLE" : "TENSION");
        return resultatStressTestRepository.save(resultat);
    }

    @Transactional(readOnly = true)
    public List<Risque> listerRisques() {
        return risqueRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<IncidentOperationnel> listerIncidents() {
        return incidentOperationnelRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<StressTest> listerStressTests() {
        return stressTestRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<ResultatStressTest> listerResultatsStressTests() {
        return resultatStressTestRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Map<String, Object> calculerTableauLiquidite() {
        BigDecimal depots = comptesVisibles().stream()
                .map(compte -> compte.getSolde().max(BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal liquiditesCaisses = caissesVisibles().stream()
                .map(Caisse::getSoldeTheorique)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal liquiditesCoffres = coffresVisibles().stream()
                .map(Coffre::getSoldeTheorique)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal encoursCredit = creditsVisibles().stream()
                .map(Credit::getCapitalRestantDu)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return Map.of(
                "depotsClients", depots,
                "liquiditesCaisses", liquiditesCaisses,
                "liquiditesCoffres", liquiditesCoffres,
                "encoursCredit", encoursCredit,
                "gapLiquidite", liquiditesCaisses.add(liquiditesCoffres).subtract(depots)
        );
    }

    private List<com.microfinance.core_banking.entity.Compte> comptesVisibles() {
        return compteRepository.findAll().stream()
                .filter(compte -> authenticatedUserService.hasGlobalScope()
                        || (authenticatedUserService.getCurrentAgencyId() != null
                        && compte.getAgence() != null
                        && authenticatedUserService.getCurrentAgencyId().equals(compte.getAgence().getIdAgence())))
                .toList();
    }

    private List<Credit> creditsVisibles() {
        return creditRepository.findAll().stream()
                .filter(credit -> {
                    Client client = credit.getClient();
                    return authenticatedUserService.hasGlobalScope()
                            || (authenticatedUserService.getCurrentAgencyId() != null
                            && client != null
                            && client.getAgence() != null
                            && authenticatedUserService.getCurrentAgencyId().equals(client.getAgence().getIdAgence()));
                })
                .toList();
    }

    private List<Caisse> caissesVisibles() {
        return caisseRepository.findAll().stream()
                .filter(caisse -> authenticatedUserService.hasGlobalScope()
                        || (authenticatedUserService.getCurrentAgencyId() != null
                        && caisse.getAgence() != null
                        && authenticatedUserService.getCurrentAgencyId().equals(caisse.getAgence().getIdAgence())))
                .toList();
    }

    private List<Coffre> coffresVisibles() {
        return coffreRepository.findAll().stream()
                .filter(coffre -> authenticatedUserService.hasGlobalScope()
                        || (authenticatedUserService.getCurrentAgencyId() != null
                        && coffre.getAgence() != null
                        && authenticatedUserService.getCurrentAgencyId().equals(coffre.getAgence().getIdAgence())))
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

    private BigDecimal decimalOrZero(Map<String, Object> payload, String key) {
        return payload.get(key) == null ? BigDecimal.ZERO : new BigDecimal(payload.get(key).toString());
    }

    private String randomSuffix() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }
}
