package com.microfinance.core_banking.service.parametrage;

import com.microfinance.core_banking.entity.Agence;
import com.microfinance.core_banking.repository.parametrage.AgenceRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

		if (modifications.getNom() != null) agence.setNom(modifications.getNom());
		if (modifications.getAdresse() != null) agence.setAdresse(modifications.getAdresse());
		if (modifications.getTelephone() != null) agence.setTelephone(modifications.getTelephone());

		return agenceRepository.save(agence);
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
	@Transactional
	public Agence desactiverAgence(Long idAgence) {
		Agence agence = agenceRepository.findById(idAgence)
				.orElseThrow(() -> new EntityNotFoundException("Agence introuvable: " + idAgence));
		agence.setEstActive(false);
		return agenceRepository.save(agence);
	}
}
