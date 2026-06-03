package com.soutra.microfinance.service.comptabilite;

import com.soutra.microfinance.entity.Compte;
import com.soutra.microfinance.entity.LigneEcriture;
import com.soutra.microfinance.entity.SensEcriture;
import com.soutra.microfinance.entity.Transaction;
import com.soutra.microfinance.repository.compte.CompteRepository;
import com.soutra.microfinance.repository.operation.LigneEcritureRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class ComptabiliteOperationnelleService {

    private final LigneEcritureRepository ligneEcritureRepository;
    private final CompteRepository compteRepository;

    public ComptabiliteOperationnelleService(
            LigneEcritureRepository ligneEcritureRepository,
            CompteRepository compteRepository
    ) {
        this.ligneEcritureRepository = ligneEcritureRepository;
        this.compteRepository = compteRepository;
    }

    @Transactional
    public LigneEcriture creerLigne(Transaction transaction, Compte compte, SensEcriture sens, BigDecimal montant) {
        LigneEcriture ligne = new LigneEcriture();
        ligne.setTransaction(transaction);
        ligne.setCompte(compte);
        ligne.setSens(sens);
        ligne.setMontant(montant);
        return ligneEcritureRepository.save(ligne);
    }

    @Transactional
    public void crediterCompte(Compte compte, BigDecimal montant) {
        compte.setSolde(compte.getSolde().add(montant));
        compteRepository.save(compte);
    }

    @Transactional
    public void debiterCompte(Compte compte, BigDecimal montant) {
        compte.setSolde(compte.getSolde().subtract(montant));
        compteRepository.save(compte);
    }
}
