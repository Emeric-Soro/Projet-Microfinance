package com.soutra.microfinance.service.parametrage;

import com.soutra.microfinance.entity.Agence;
import com.soutra.microfinance.repository.parametrage.AgenceRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import com.soutra.microfinance.audit.AuditContext;

@Service
public class AgenceServiceImpl implements AgenceService {

	private final AgenceRepository agenceRepository;

	public AgenceServiceImpl(AgenceRepository agenceRepository) {
		this.agenceRepository = agenceRepository;
	}

	@Override
	@Transactional
	public Agence creerAgence(Agence agence) {
		if (agenceRepository.existsByCodeAgence(agence.getCodeAgence())) {
			throw new IllegalArgumentException("Le code agence '" + agence.getCodeAgence() + "' existe deja.");
		}
		agence.setEstActive(true);
		return agenceRepository.save(agence);
	}

	@Override
	@Transactional
	public Agence modifierAgence(Long idAgence, Agence modifications) {
		Agence agence = agenceRepository.findById(idAgence)
				.orElseThrow(() -> new EntityNotFoundException("Agence introuvable: " + idAgence));

		AuditContext.setIdEntite(String.valueOf(idAgence));
		java.util.Map<String, Object> avant = new java.util.HashMap<>();
		avant.put("nom", agence.getNom());
		avant.put("adresse", agence.getAdresse());
		avant.put("telephone", agence.getTelephone());
		AuditContext.setDetailsAvant(AuditContext.toJson(avant));

		if (modifications.getNom() != null) agence.setNom(modifications.getNom());
		if (modifications.getAdresse() != null) agence.setAdresse(modifications.getAdresse());
		if (modifications.getTelephone() != null) agence.setTelephone(modifications.getTelephone());

		Agence saved = agenceRepository.save(agence);

		java.util.Map<String, Object> apres = new java.util.HashMap<>();
		apres.put("nom", saved.getNom());
		apres.put("adresse", saved.getAdresse());
		apres.put("telephone", saved.getTelephone());
		AuditContext.setDetailsApres(AuditContext.toJson(apres));

		return saved;
	}

	@Override
	@Transactional(readOnly = true)
	public Agence obtenirAgence(Long idAgence) {
		return agenceRepository.findById(idAgence)
				.orElseThrow(() -> new EntityNotFoundException("Agence introuvable: " + idAgence));
	}

	@Override
	@Transactional(readOnly = true)
	public List<Agence> listerAgencesActives() {
		return agenceRepository.findByEstActiveTrue();
	}

	@Override
	@Transactional(readOnly = true)
	public Page<Agence> listerAgencesPagine(Pageable pageable) {
		return agenceRepository.findAllByOrderByNomAsc(pageable);
	}

	@Override
	@Transactional
	public Agence desactiverAgence(Long idAgence) {
		Agence agence = agenceRepository.findById(idAgence)
				.orElseThrow(() -> new EntityNotFoundException("Agence introuvable: " + idAgence));

		AuditContext.setIdEntite(String.valueOf(idAgence));
		java.util.Map<String, Object> avant = new java.util.HashMap<>();
		avant.put("estActive", agence.getEstActive());
		AuditContext.setDetailsAvant(AuditContext.toJson(avant));

		agence.setEstActive(false);
		Agence saved = agenceRepository.save(agence);

		java.util.Map<String, Object> apres = new java.util.HashMap<>();
		apres.put("estActive", false);
		AuditContext.setDetailsApres(AuditContext.toJson(apres));

		return saved;
	}
}
