package com.microfinance.core_banking.service.extension;

import com.microfinance.core_banking.dto.request.extension.CommanderChequierRequestDTO;
import com.microfinance.core_banking.dto.request.extension.OpposerChequierRequestDTO;
import com.microfinance.core_banking.dto.request.extension.RemettreChequeRequestDTO;
import com.microfinance.core_banking.entity.Chequier;
import com.microfinance.core_banking.entity.Compte;
import com.microfinance.core_banking.entity.RemiseCheque;
import com.microfinance.core_banking.repository.compte.CompteRepository;
import com.microfinance.core_banking.repository.extension.ChequierRepository;
import com.microfinance.core_banking.repository.extension.RemiseChequeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ChequierExtensionService {

    private final ChequierRepository chequierRepository;
    private final RemiseChequeRepository remiseChequeRepository;
    private final CompteRepository compteRepository;

    public ChequierExtensionService(
            ChequierRepository chequierRepository,
            RemiseChequeRepository remiseChequeRepository,
            CompteRepository compteRepository
    ) {
        this.chequierRepository = chequierRepository;
        this.remiseChequeRepository = remiseChequeRepository;
        this.compteRepository = compteRepository;
    }

    @Transactional
    public Chequier commander(CommanderChequierRequestDTO dto) {
        Compte compte = compteRepository.findById(dto.getIdCompte())
                .orElseThrow(() -> new EntityNotFoundException("Compte introuvable"));
        Chequier chequier = new Chequier();
        chequier.setNumeroChequier("CHQ-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase());
        chequier.setCompte(compte);
        chequier.setNombreCheques(dto.getNombreCheques());
        chequier.setPremierNumero(dto.getPremierNumero());
        int dernier = Integer.parseInt(dto.getPremierNumero()) + dto.getNombreCheques() - 1;
        chequier.setDernierNumero(String.valueOf(dernier));
        chequier.setDateCommande(LocalDateTime.now());
        chequier.setStatut("COMMANDE");
        return chequierRepository.save(chequier);
    }

    @Transactional
    public RemiseCheque remettreCheque(RemettreChequeRequestDTO dto) {
        Chequier chequier = chequierRepository.findById(dto.getIdChequier())
                .orElseThrow(() -> new EntityNotFoundException("Chequier introuvable"));
        Compte compteRemise = compteRepository.findById(dto.getCompteRemise())
                .orElseThrow(() -> new EntityNotFoundException("Compte remise introuvable"));
        RemiseCheque remise = new RemiseCheque();
        remise.setChequier(chequier);
        remise.setNumeroCheque(dto.getNumeroCheque());
        remise.setMontant(dto.getMontant());
        remise.setTireur(dto.getTireur());
        remise.setCompteRemise(compteRemise);
        remise.setDateRemise(LocalDateTime.now());
        remise.setStatut("REMIS");
        return remiseChequeRepository.save(remise);
    }

    @Transactional
    public Chequier opposerChequier(Long idChequier, OpposerChequierRequestDTO dto) {
        Chequier chequier = chequierRepository.findById(idChequier)
                .orElseThrow(() -> new EntityNotFoundException("Chequier introuvable"));
        chequier.setStatut("OPPOSE");
        chequier.setMotifOpposition(dto.getMotifOpposition());
        return chequierRepository.save(chequier);
    }

    @Transactional
    public RemiseCheque encaisserCheque(Long idRemiseCheque) {
        RemiseCheque remise = remiseChequeRepository.findById(idRemiseCheque)
                .orElseThrow(() -> new EntityNotFoundException("Remise cheque introuvable"));
        remise.setStatut("ENCAISSE");
        remise.setDateValeur(java.time.LocalDate.now());
        remise.setReferenceTransaction("ENC-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase());
        return remiseChequeRepository.save(remise);
    }

    @Transactional(readOnly = true)
    public List<Chequier> listerChequiersByCompte(Long idCompte) {
        return chequierRepository.findByCompte_IdCompte(idCompte);
    }

    @Transactional(readOnly = true)
    public List<RemiseCheque> listerRemisesByChequier(Long idChequier) {
        return remiseChequeRepository.findByChequier_IdChequier(idChequier);
    }
}
