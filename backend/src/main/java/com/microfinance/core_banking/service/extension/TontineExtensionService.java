package com.microfinance.core_banking.service.extension;

import com.microfinance.core_banking.dto.request.extension.CotiserTontineRequestDTO;
import com.microfinance.core_banking.dto.request.extension.CreerTontineRequestDTO;
import com.microfinance.core_banking.entity.Agence;
import com.microfinance.core_banking.entity.Client;
import com.microfinance.core_banking.entity.CotisationTontine;
import com.microfinance.core_banking.entity.Tontine;
import com.microfinance.core_banking.entity.TourTontine;
import com.microfinance.core_banking.repository.client.ClientRepository;
import com.microfinance.core_banking.repository.extension.AgenceRepository;
import com.microfinance.core_banking.repository.extension.CotisationTontineRepository;
import com.microfinance.core_banking.repository.extension.TontineRepository;
import com.microfinance.core_banking.repository.extension.TourTontineRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class TontineExtensionService {

    private final TontineRepository tontineRepository;
    private final TourTontineRepository tourTontineRepository;
    private final CotisationTontineRepository cotisationTontineRepository;
    private final AgenceRepository agenceRepository;
    private final ClientRepository clientRepository;

    public TontineExtensionService(
            TontineRepository tontineRepository,
            TourTontineRepository tourTontineRepository,
            CotisationTontineRepository cotisationTontineRepository,
            AgenceRepository agenceRepository,
            ClientRepository clientRepository
    ) {
        this.tontineRepository = tontineRepository;
        this.tourTontineRepository = tourTontineRepository;
        this.cotisationTontineRepository = cotisationTontineRepository;
        this.agenceRepository = agenceRepository;
        this.clientRepository = clientRepository;
    }

    @Transactional
    public Tontine creerTontine(CreerTontineRequestDTO dto) {
        Agence agence = agenceRepository.findById(dto.getIdAgence())
                .orElseThrow(() -> new EntityNotFoundException("Agence introuvable"));
        Tontine tontine = new Tontine();
        tontine.setCodeTontine(dto.getCodeTontine());
        tontine.setIntitule(dto.getIntitule());
        tontine.setTypeTontine(dto.getTypeTontine());
        tontine.setMontantCotisation(dto.getMontantCotisation());
        tontine.setPeriodicite(dto.getPeriodicite());
        tontine.setNombreParticipants(dto.getNombreParticipants());
        tontine.setDateDebut(dto.getDateDebut());
        tontine.setAgence(agence);
        tontine.setStatut("ACTIVE");
        return tontineRepository.save(tontine);
    }

    @Transactional
    public TourTontine demarrerTour(Long idTontine) {
        Tontine tontine = tontineRepository.findById(idTontine)
                .orElseThrow(() -> new EntityNotFoundException("Tontine introuvable"));
        List<TourTontine> toursExistants = tourTontineRepository.findByTontine_IdTontineOrderByNumeroTourAsc(idTontine);
        int prochainNumero = toursExistants.isEmpty() ? 1 : toursExistants.get(toursExistants.size() - 1).getNumeroTour() + 1;

        List<Client> participants = clientRepository.findAll();
        int indexBeneficiaire = (prochainNumero - 1) % Math.max(participants.size(), 1);

        TourTontine tour = new TourTontine();
        tour.setTontine(tontine);
        tour.setNumeroTour(prochainNumero);
        tour.setDateTour(tontine.getDateDebut().plusMonths(prochainNumero - 1));
        tour.setBeneficiaire(participants.isEmpty() ? null : participants.get(indexBeneficiaire));
        tour.setMontantCollecte(BigDecimal.ZERO);
        tour.setStatut("EN_ATTENTE");
        return tourTontineRepository.save(tour);
    }

    @Transactional
    public CotisationTontine cotiser(CotiserTontineRequestDTO dto) {
        TourTontine tour = tourTontineRepository.findById(dto.getIdTourTontine())
                .orElseThrow(() -> new EntityNotFoundException("Tour tontine introuvable"));
        Client participant = clientRepository.findById(dto.getIdParticipant())
                .orElseThrow(() -> new EntityNotFoundException("Participant introuvable"));
        CotisationTontine cotisation = new CotisationTontine();
        cotisation.setTourTontine(tour);
        cotisation.setParticipant(participant);
        cotisation.setMontantCotise(dto.getMontantCotise());
        cotisation.setDateCotisation(dto.getDateCotisation());
        cotisation.setStatut("PAYEE");
        CotisationTontine sauvegardee = cotisationTontineRepository.save(cotisation);

        BigDecimal totalCollecte = cotisationTontineRepository.findByTourTontine_IdTourTontine(tour.getIdTourTontine())
                .stream()
                .map(c -> c.getMontantCotise() != null ? c.getMontantCotise() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        tour.setMontantCollecte(totalCollecte);
        tourTontineRepository.save(tour);
        return sauvegardee;
    }

    @Transactional(readOnly = true)
    public List<Tontine> listerTontines() {
        return tontineRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<TourTontine> listerTours(Long idTontine) {
        return tourTontineRepository.findByTontine_IdTontineOrderByNumeroTourAsc(idTontine);
    }

    @Transactional(readOnly = true)
    public List<CotisationTontine> listerCotisations(Long idTour) {
        return cotisationTontineRepository.findByTourTontine_IdTourTontine(idTour);
    }
}
