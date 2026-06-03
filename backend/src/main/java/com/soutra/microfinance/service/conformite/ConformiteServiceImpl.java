package com.soutra.microfinance.service.conformite;

import com.soutra.microfinance.dto.request.conformite.ConsentementRgpdRequestDTO;
import com.soutra.microfinance.dto.request.conformite.CreateAlerteLcbFtRequestDTO;
import com.soutra.microfinance.dto.request.conformite.CreateRapportSarRequestDTO;
import com.soutra.microfinance.dto.request.conformite.CreateReclamationRequestDTO;
import com.soutra.microfinance.dto.request.conformite.TraiterAlerteLcbFtRequestDTO;
import com.soutra.microfinance.dto.request.conformite.TraiterReclamationRequestDTO;
import com.soutra.microfinance.dto.request.conformite.UpdateSarStatusRequestDTO;
import com.soutra.microfinance.dto.request.conformite.VerifierPepRequestDTO;
import com.soutra.microfinance.dto.response.conformite.AlerteLcbFtResponseDTO;
import com.soutra.microfinance.dto.response.conformite.ConsentementRgpdResponseDTO;
import com.soutra.microfinance.dto.response.conformite.KycExpireResponseDTO;
import com.soutra.microfinance.dto.response.conformite.PepResponseDTO;
import com.soutra.microfinance.dto.response.conformite.RapportSarResponseDTO;
import com.soutra.microfinance.dto.response.conformite.ReclamationResponseDTO;
import com.soutra.microfinance.entity.conformite.AlerteLcbFt;
import com.soutra.microfinance.entity.conformite.ConsentementRgpd;
import com.soutra.microfinance.entity.conformite.PersonnePolitiquementExposee;
import com.soutra.microfinance.entity.conformite.RapportSarsCentif;
import com.soutra.microfinance.entity.conformite.Reclamation;
import com.soutra.microfinance.entity.Client;
import com.soutra.microfinance.entity.StatutKycClient;
import com.soutra.microfinance.repository.client.ClientRepository;
import com.soutra.microfinance.repository.conformite.AlerteLcbFtRepository;
import com.soutra.microfinance.repository.conformite.ConsentementRgpdRepository;
import com.soutra.microfinance.repository.conformite.PersonnePolitiquementExposeeRepository;
import com.soutra.microfinance.repository.conformite.RapportSarsCentifRepository;
import com.soutra.microfinance.repository.conformite.ReclamationRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ConformiteServiceImpl implements ConformiteService {

    private final RapportSarsCentifRepository rapportSarsCentifRepository;
    private final ReclamationRepository reclamationRepository;
    private final ConsentementRgpdRepository consentementRgpdRepository;
    private final PersonnePolitiquementExposeeRepository personnePolitiquementExposeeRepository;
    private final AlerteLcbFtRepository alerteLcbFtRepository;
    private final ClientRepository clientRepository;

    public ConformiteServiceImpl(
            RapportSarsCentifRepository rapportSarsCentifRepository,
            ReclamationRepository reclamationRepository,
            ConsentementRgpdRepository consentementRgpdRepository,
            PersonnePolitiquementExposeeRepository personnePolitiquementExposeeRepository,
            AlerteLcbFtRepository alerteLcbFtRepository,
            ClientRepository clientRepository
    ) {
        this.rapportSarsCentifRepository = rapportSarsCentifRepository;
        this.reclamationRepository = reclamationRepository;
        this.consentementRgpdRepository = consentementRgpdRepository;
        this.personnePolitiquementExposeeRepository = personnePolitiquementExposeeRepository;
        this.alerteLcbFtRepository = alerteLcbFtRepository;
        this.clientRepository = clientRepository;
    }

    // ==================== RAPPORTS SAR / CENTIF ====================

    @Override
    @Transactional
    public RapportSarResponseDTO creerRapportSar(CreateRapportSarRequestDTO dto) {
        RapportSarsCentif rapport = new RapportSarsCentif();
        rapport.setReference("SAR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        rapport.setIdClient(dto.getIdClient());
        rapport.setTypeAlerte(dto.getTypeAlerte());
        rapport.setDescription(dto.getDescription());
        rapport.setMontantSoupconne(dto.getMontantSoupconne());
        rapport.setStatut("NOUVEAU");
        rapport.setDateCreation(LocalDateTime.now());
        rapport.setSoumisPar(dto.getSoumisPar());
        rapport.setTransmissionCentif(false);

        RapportSarsCentif saved = rapportSarsCentifRepository.save(rapport);
        return toRapportSarResponseDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RapportSarResponseDTO> listerRapportsSar(Pageable pageable) {
        return rapportSarsCentifRepository.findAll(pageable).map(this::toRapportSarResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public RapportSarResponseDTO getRapportSar(Long id) {
        RapportSarsCentif rapport = rapportSarsCentifRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rapport SAR introuvable: " + id));
        return toRapportSarResponseDTO(rapport);
    }

    @Override
    @Transactional
    public RapportSarResponseDTO mettreAJourStatutSar(Long id, UpdateSarStatusRequestDTO dto) {
        RapportSarsCentif rapport = rapportSarsCentifRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rapport SAR introuvable: " + id));

        rapport.setStatut(dto.getStatut());
        rapport.setTraitePar(dto.getTraitePar());
        rapport.setDateTraitement(LocalDateTime.now());
        rapport.setMotifRejet(dto.getMotifRejet());

        if ("TRANSMIS".equalsIgnoreCase(dto.getStatut())) {
            rapport.setTransmissionCentif(true);
        }

        RapportSarsCentif saved = rapportSarsCentifRepository.save(rapport);
        return toRapportSarResponseDTO(saved);
    }

    // ==================== RECLAMATIONS ====================

    @Override
    @Transactional
    public ReclamationResponseDTO creerReclamation(CreateReclamationRequestDTO dto) {
        Reclamation reclamation = new Reclamation();
        reclamation.setReference("RECL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        reclamation.setIdClient(dto.getIdClient());
        reclamation.setTypeReclamation(dto.getTypeReclamation());
        reclamation.setDescription(dto.getDescription());
        reclamation.setStatut("NOUVEAU");
        reclamation.setPriorite(dto.getPriorite() != null ? dto.getPriorite() : "NORMALE");
        reclamation.setDateCreation(LocalDateTime.now());
        reclamation.setCreePar(dto.getCreePar());

        Reclamation saved = reclamationRepository.save(reclamation);
        return toReclamationResponseDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReclamationResponseDTO> listerReclamations(Pageable pageable) {
        return reclamationRepository.findAll(pageable).map(this::toReclamationResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public ReclamationResponseDTO getReclamation(Long id) {
        Reclamation reclamation = reclamationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reclamation introuvable: " + id));
        return toReclamationResponseDTO(reclamation);
    }

    @Override
    @Transactional
    public ReclamationResponseDTO traiterReclamation(Long id, TraiterReclamationRequestDTO dto) {
        Reclamation reclamation = reclamationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reclamation introuvable: " + id));

        reclamation.setStatut(dto.getStatut());
        reclamation.setTraitePar(dto.getTraitePar());
        reclamation.setDateTraitement(LocalDateTime.now());
        reclamation.setMotifCloture(dto.getMotifCloture());

        Reclamation saved = reclamationRepository.save(reclamation);
        return toReclamationResponseDTO(saved);
    }

    // ==================== RGPD / CONSENTEMENT ====================

    @Override
    @Transactional
    public void enregistrerConsentement(ConsentementRgpdRequestDTO dto) {
        ConsentementRgpd consentement = new ConsentementRgpd();
        consentement.setIdClient(dto.getIdClient());
        consentement.setFinalite(dto.getFinalite());
        consentement.setConsenti(dto.getConsenti());
        consentement.setDateConsentement(LocalDateTime.now());
        consentement.setAdresseIp(dto.getAdresseIp());

        if (Boolean.TRUE.equals(dto.getConsenti())) {
            consentement.setDateExpiration(LocalDateTime.now().plusYears(1));
        }

        consentementRgpdRepository.save(consentement);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConsentementRgpdResponseDTO> exporterDonneesPersonnelles(Long idClient) {
        List<ConsentementRgpd> consentements = consentementRgpdRepository.findByIdClient(idClient);
        return consentements.stream()
                .map(c -> new ConsentementRgpdResponseDTO(
                        c.getIdConsentement(),
                        c.getIdClient(),
                        c.getFinalite(),
                        c.getConsenti(),
                        c.getDateConsentement(),
                        c.getDateExpiration()
                ))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void effacerDonnees(Long idClient) {
        List<ConsentementRgpd> consentements = consentementRgpdRepository.findByIdClient(idClient);
        consentementRgpdRepository.deleteAll(consentements);
    }

    // ==================== KYC EXPIRE ====================

    @Override
    @Transactional(readOnly = true)
    public List<KycExpireResponseDTO> listerKycExpires() {
        LocalDateTime dateLimite = LocalDateTime.now().minusDays(30);
        List<Client> clients = clientRepository.findByDateSoumissionKycBefore(
                dateLimite.toLocalDate()
        );
        return clients.stream()
                .filter(c -> c.getStatutKyc() == StatutKycClient.EN_ATTENTE
                        || c.getStatutKyc() == StatutKycClient.BROUILLON)
                .map(this::toKycExpireResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<KycExpireResponseDTO> listerKycExpires(Pageable pageable) {
        return clientRepository.findAll(pageable)
                .map(this::toKycExpireResponseDTO);
    }

    // ==================== PEP ====================

    @Override
    @Transactional
    public PepResponseDTO verifierPep(VerifierPepRequestDTO dto) {
        PersonnePolitiquementExposee pep = personnePolitiquementExposeeRepository.findByIdClient(dto.getIdClient())
                .orElseGet(() -> {
                    PersonnePolitiquementExposee newPep = new PersonnePolitiquementExposee();
                    newPep.setIdClient(dto.getIdClient());
                    newPep.setDateDeclaration(LocalDateTime.now());
                    return newPep;
                });

        pep.setSourceInformation(dto.getSourceInformation());
        pep.setVerifiePar(dto.getVerifiePar());
        pep.setDateVerification(LocalDateTime.now());
        pep.setStatut("ACTIF");

        PersonnePolitiquementExposee saved = personnePolitiquementExposeeRepository.save(pep);
        return toPepResponseDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PepResponseDTO> listerPep() {
        return personnePolitiquementExposeeRepository.findAll().stream()
                .map(this::toPepResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PepResponseDTO> listerPep(Pageable pageable) {
        return personnePolitiquementExposeeRepository.findAll(pageable)
                .map(this::toPepResponseDTO);
    }

    // ==================== ALERTES LCB-FT ====================

    @Override
    @Transactional
    public AlerteLcbFtResponseDTO creerAlerte(CreateAlerteLcbFtRequestDTO dto) {
        AlerteLcbFt alerte = new AlerteLcbFt();
        alerte.setIdClient(dto.getIdClient());
        alerte.setTypeAlerte(dto.getTypeAlerte());
        alerte.setDescription(dto.getDescription());
        alerte.setNiveauRisque(dto.getNiveauRisque());
        alerte.setStatut("OUVERTE");
        alerte.setDateCreation(LocalDateTime.now());

        AlerteLcbFt saved = alerteLcbFtRepository.save(alerte);
        return toAlerteLcbFtResponseDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlerteLcbFtResponseDTO> listerAlertesLcbFt() {
        return alerteLcbFtRepository.findAll().stream()
                .map(this::toAlerteLcbFtResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AlerteLcbFtResponseDTO> listerAlertesLcbFt(Pageable pageable) {
        return alerteLcbFtRepository.findAll(pageable)
                .map(this::toAlerteLcbFtResponseDTO);
    }

    @Override
    @Transactional
    public AlerteLcbFtResponseDTO traiterAlerte(Long id, TraiterAlerteLcbFtRequestDTO dto) {
        AlerteLcbFt alerte = alerteLcbFtRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Alerte LCB-FT introuvable: " + id));

        alerte.setStatut(dto.getStatut());
        alerte.setTraitePar(dto.getTraitePar());
        alerte.setDateTraitement(LocalDateTime.now());
        alerte.setActions(dto.getActions());

        AlerteLcbFt saved = alerteLcbFtRepository.save(alerte);
        return toAlerteLcbFtResponseDTO(saved);
    }

    // ==================== MAPPEURS PRIVES ====================

    private RapportSarResponseDTO toRapportSarResponseDTO(RapportSarsCentif r) {
        return new RapportSarResponseDTO(
                r.getIdRapport(),
                r.getReference(),
                r.getIdClient(),
                r.getTypeAlerte(),
                r.getDescription(),
                r.getMontantSoupconne(),
                r.getStatut(),
                r.getDateCreation(),
                r.getDateTraitement(),
                r.getTransmissionCentif()
        );
    }

    private ReclamationResponseDTO toReclamationResponseDTO(Reclamation r) {
        return new ReclamationResponseDTO(
                r.getIdReclamation(),
                r.getReference(),
                r.getIdClient(),
                r.getTypeReclamation(),
                r.getDescription(),
                r.getStatut(),
                r.getPriorite(),
                r.getDateCreation(),
                r.getDateTraitement()
        );
    }

    private PepResponseDTO toPepResponseDTO(PersonnePolitiquementExposee p) {
        return new PepResponseDTO(
                p.getIdPep(),
                p.getIdClient(),
                p.getNomComplet(),
                p.getFonction(),
                p.getPays(),
                p.getNiveauRisque(),
                p.getStatut(),
                p.getDateDeclaration()
        );
    }

    private AlerteLcbFtResponseDTO toAlerteLcbFtResponseDTO(AlerteLcbFt a) {
        return new AlerteLcbFtResponseDTO(
                a.getIdAlerte(),
                a.getIdClient(),
                a.getTypeAlerte(),
                a.getDescription(),
                a.getNiveauRisque(),
                a.getStatut(),
                a.getDateCreation()
        );
    }

    private KycExpireResponseDTO toKycExpireResponseDTO(Client c) {
        return new KycExpireResponseDTO(
                c.getIdClient(),
                c.getCodeClient(),
                c.getNom() + " " + c.getPrenom(),
                c.getDateSoumissionKyc(),
                c.getStatutKyc().name(),
                c.getNiveauRisque().name(),
                c.getDateSoumissionKyc() != null
                        ? java.time.temporal.ChronoUnit.DAYS.between(c.getDateSoumissionKyc(), LocalDate.now())
                        : 0
        );
    }
}
