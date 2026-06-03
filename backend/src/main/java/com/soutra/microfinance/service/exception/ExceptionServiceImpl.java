package com.soutra.microfinance.service.exception;

import com.soutra.microfinance.dto.request.parametrage.*;
import com.soutra.microfinance.dto.response.common.*;
import com.soutra.microfinance.entity.Derogation;
import com.soutra.microfinance.entity.Escalade;
import com.soutra.microfinance.repository.exception.DerogationRepository;
import com.soutra.microfinance.repository.exception.EscaladeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ExceptionServiceImpl implements ExceptionService {

    private final DerogationRepository derogationRepository;
    private final EscaladeRepository escaladeRepository;

    public ExceptionServiceImpl(DerogationRepository derogationRepository, EscaladeRepository escaladeRepository) {
        this.derogationRepository = derogationRepository;
        this.escaladeRepository = escaladeRepository;
    }

    @Override
    @Transactional
    public DerogationResponseDTO creerDerogation(DerogationRequestDTO requestDTO, String creePar) {
        Derogation derogation = new Derogation();
        derogation.setReference("DER-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        derogation.setTypeDerogation(requestDTO.getTypeDerogation());
        derogation.setDescription(requestDTO.getDescription());
        derogation.setMotif(requestDTO.getMotif());
        derogation.setMontantConcerne(requestDTO.getMontantConcerne());
        derogation.setIdClient(requestDTO.getIdClient());
        derogation.setIdTransaction(requestDTO.getIdTransaction());
        derogation.setStatut("SOUMISE");
        derogation.setDateCreation(LocalDateTime.now());
        derogation.setCreePar(creePar);

        Derogation saved = derogationRepository.save(derogation);
        return toDerogationResponseDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DerogationResponseDTO> listerDerogations() {
        return derogationRepository.findAllByOrderByDateCreationDesc()
                .stream()
                .map(this::toDerogationResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DerogationResponseDTO> listerDerogations(Pageable pageable) {
        return derogationRepository.findAllByOrderByDateCreationDesc(pageable)
                .map(this::toDerogationResponseDTO);
    }

    @Override
    @Transactional
    public DerogationResponseDTO traiterDerogation(Long id, TraiterDerogationRequestDTO requestDTO, String traitePar) {
        Derogation derogation = derogationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Derogation introuvable: " + id));

        derogation.setStatut(requestDTO.getStatut());
        derogation.setMotifTraitement(requestDTO.getMotifTraitement());
        derogation.setDateTraitement(LocalDateTime.now());
        derogation.setTraitePar(traitePar);

        Derogation saved = derogationRepository.save(derogation);
        return toDerogationResponseDTO(saved);
    }

    @Override
    @Transactional
    public EscaladeResponseDTO creerEscalade(EscaladeRequestDTO requestDTO, String creePar) {
        Escalade escalade = new Escalade();
        escalade.setReference("ESC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        escalade.setTypeEscalade(requestDTO.getTypeEscalade());
        escalade.setDescription(requestDTO.getDescription());
        escalade.setNiveau(requestDTO.getNiveau() != null ? requestDTO.getNiveau() : "N1");
        escalade.setStatut("OUVERTE");
        escalade.setIdClient(requestDTO.getIdClient());
        escalade.setIdTransaction(requestDTO.getIdTransaction());
        escalade.setDateCreation(LocalDateTime.now());
        escalade.setCreePar(creePar);

        Escalade saved = escaladeRepository.save(escalade);
        return toEscaladeResponseDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EscaladeResponseDTO> listerEscalades() {
        return escaladeRepository.findAllByOrderByDateCreationDesc()
                .stream()
                .map(this::toEscaladeResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EscaladeResponseDTO> listerEscalades(Pageable pageable) {
        return escaladeRepository.findAllByOrderByDateCreationDesc(pageable)
                .map(this::toEscaladeResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public EscaladeResponseDTO getEscaladeById(Long id) {
        Escalade escalade = escaladeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Escalade introuvable: " + id));
        return toEscaladeResponseDTO(escalade);
    }

    @Override
    @Transactional
    public EscaladeResponseDTO traiterEscalade(Long id, TraiterEscaladeRequestDTO requestDTO, String traitePar) {
        Escalade escalade = escaladeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Escalade introuvable: " + id));

        escalade.setAction(requestDTO.getAction());
        escalade.setCommentaire(requestDTO.getCommentaire());
        escalade.setDateTraitement(LocalDateTime.now());
        escalade.setTraitePar(traitePar);
        escalade.setStatut("TRAITEE");

        // Progression N1 -> N2 -> N3 -> N4
        String niveauActuel = escalade.getNiveau();
        switch (niveauActuel) {
            case "N1":
                escalade.setNiveau("N2");
                break;
            case "N2":
                escalade.setNiveau("N3");
                break;
            case "N3":
                escalade.setNiveau("N4");
                break;
            default:
                escalade.setStatut("RESOLUE");
                break;
        }

        Escalade saved = escaladeRepository.save(escalade);
        return toEscaladeResponseDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegleDerogationEscaladeResponseDTO> listerRegles() {
        return List.of(
                new RegleDerogationEscaladeResponseDTO(1L, "DEROGATION", "MONTANT", "APPROBATION", new BigDecimal("500000"), "SUPERVISEUR"),
                new RegleDerogationEscaladeResponseDTO(2L, "DEROGATION", "MONTANT", "APPROBATION", new BigDecimal("2000000"), "ADMIN"),
                new RegleDerogationEscaladeResponseDTO(3L, "ESCALADE", "NIVEAU_RISQUE", "ESCALADE_N1", new BigDecimal("1000000"), "CHEF_AGENCE"),
                new RegleDerogationEscaladeResponseDTO(4L, "ESCALADE", "NIVEAU_RISQUE", "ESCALADE_N2", new BigDecimal("5000000"), "SUPERVISEUR"),
                new RegleDerogationEscaladeResponseDTO(5L, "ESCALADE", "NIVEAU_RISQUE", "ESCALADE_N3", new BigDecimal("10000000"), "ADMIN")
        );
    }

    private DerogationResponseDTO toDerogationResponseDTO(Derogation d) {
        return new DerogationResponseDTO(
                d.getIdDerogation(),
                d.getReference(),
                d.getTypeDerogation(),
                d.getDescription(),
                d.getMotif(),
                d.getMontantConcerne(),
                d.getStatut(),
                d.getDateCreation(),
                d.getCreePar(),
                d.getDateTraitement(),
                d.getTraitePar()
        );
    }

    private EscaladeResponseDTO toEscaladeResponseDTO(Escalade e) {
        return new EscaladeResponseDTO(
                e.getIdEscalade(),
                e.getReference(),
                e.getTypeEscalade(),
                e.getDescription(),
                e.getNiveau(),
                e.getStatut(),
                e.getDateCreation(),
                e.getCreePar(),
                e.getDateTraitement(),
                e.getTraitePar()
        );
    }
}
