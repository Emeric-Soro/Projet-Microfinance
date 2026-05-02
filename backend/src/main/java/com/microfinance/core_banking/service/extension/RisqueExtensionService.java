package com.microfinance.core_banking.service.extension;

import com.microfinance.core_banking.dto.request.extension.CreerRisqueServiceRequestDTO;
import com.microfinance.core_banking.dto.request.extension.CreerStressTestServiceRequestDTO;
import com.microfinance.core_banking.dto.request.extension.DeclarerIncidentServiceRequestDTO;
import com.microfinance.core_banking.dto.response.extension.TableauLiquiditeResponseDTO;
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
    public Risque creerRisque(CreerRisqueServiceRequestDTO dto) {
        Risque risque = new Risque();
        risque.setCodeRisque(dto.getCodeRisque() == null ? "RSK-" + randomSuffix() : dto.getCodeRisque());
        risque.setCategorie(dto.getCategorie());
        risque.setLibelle(dto.getLibelle());
        risque.setNiveau(dto.getNiveau());
        risque.setStatut(dto.getStatut());
        return risqueRepository.save(risque);
    }

    @Transactional
    public IncidentOperationnel declarerIncident(DeclarerIncidentServiceRequestDTO dto) {
        IncidentOperationnel incident = new IncidentOperationnel();
        incident.setReferenceIncident(dto.getReferenceIncident() == null ? "INC-" + randomSuffix() : dto.getReferenceIncident());
        incident.setTypeIncident(dto.getTypeIncident());
        incident.setGravite(dto.getGravite());
        incident.setStatut(dto.getStatut());
        incident.setDescription(dto.getDescription());
        if (dto.getIdRisque() != null) {
            Risque risque = risqueRepository.findById(Long.valueOf(dto.getIdRisque()))
                    .orElseThrow(() -> new EntityNotFoundException("Risque introuvable"));
            incident.setRisque(risque);
        }
        return incidentOperationnelRepository.save(incident);
    }

    @Transactional
    public StressTest creerStressTest(CreerStressTestServiceRequestDTO dto) {
        StressTest stressTest = new StressTest();
        stressTest.setCodeScenario(dto.getCodeScenario() == null ? "ST-" + randomSuffix() : dto.getCodeScenario());
        stressTest.setLibelle(dto.getLibelle());
        stressTest.setTauxDefaut(dto.getTauxDefaut());
        stressTest.setTauxRetrait(dto.getTauxRetrait());
        stressTest.setStatut(dto.getStatut());
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
    public TableauLiquiditeResponseDTO calculerTableauLiquidite() {
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

        Map<String, Object> lignes = new java.util.LinkedHashMap<>();
        lignes.put("depotsClients", depots);
        lignes.put("liquiditesCaisses", liquiditesCaisses);
        lignes.put("liquiditesCoffres", liquiditesCoffres);
        lignes.put("encoursCredit", encoursCredit);
        lignes.put("gapLiquidite", liquiditesCaisses.add(liquiditesCoffres).subtract(depots));

        TableauLiquiditeResponseDTO response = new TableauLiquiditeResponseDTO();
        response.setLignes(List.of(lignes));
        return response;
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

    private String randomSuffix() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }
}
