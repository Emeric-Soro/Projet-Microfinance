package com.soutra.microfinance.service.comptabilite;

import com.soutra.microfinance.dto.comptabilite.LigneEcritureDTO;
import com.soutra.microfinance.entity.comptabilite.CompteComptable;
import com.soutra.microfinance.entity.comptabilite.EcritureComptable;
import com.soutra.microfinance.entity.comptabilite.TransactionComptable;
import com.soutra.microfinance.repository.comptabilite.CompteComptableRepository;
import com.soutra.microfinance.repository.comptabilite.EcritureComptableRepository;
import com.soutra.microfinance.repository.comptabilite.TransactionComptableRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class MoteurComptableService {

    private final CompteComptableRepository compteComptableRepository;
    private final TransactionComptableRepository transactionComptableRepository;
    private final EcritureComptableRepository ecritureComptableRepository;

    public MoteurComptableService(
            CompteComptableRepository compteComptableRepository,
            TransactionComptableRepository transactionComptableRepository,
            EcritureComptableRepository ecritureComptableRepository
    ) {
        this.compteComptableRepository = compteComptableRepository;
        this.transactionComptableRepository = transactionComptableRepository;
        this.ecritureComptableRepository = ecritureComptableRepository;
    }

    /**
     * Enregistre une ecriture comptable en partie double au grand livre SYSCOHADA.
     *
     * @param lignes           liste des lignes d'ecriture (debit/credit par compte)
     * @param libelle          libelle descriptif de l'operation
     * @param referenceOperateur reference de l'operateur ayant genere l'ecriture
     * @return la transaction comptable creee
     * @throws IllegalStateException si l'ecriture n'est pas equilibree
     */
    @Transactional
    public TransactionComptable enregistrerEcriture(
            List<LigneEcritureDTO> lignes,
            String libelle,
            String referenceOperateur
    ) {
        validerEcritures(lignes);

        // Creation de l'entete de transaction
        TransactionComptable transaction = new TransactionComptable();
        transaction.setDateTransaction(LocalDateTime.now());
        transaction.setLibelle(libelle);
        transaction.setReferenceOperateur(referenceOperateur);
        transaction.setEcritures(new ArrayList<>());
        transaction = transactionComptableRepository.save(transaction);

        // Enregistrement des lignes et mise a jour des soldes
        for (LigneEcritureDTO ligne : lignes) {
            CompteComptable compte = compteComptableRepository.findById(ligne.numeroCompte())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Compte comptable introuvable : " + ligne.numeroCompte()));

            EcritureComptable ecriture = new EcritureComptable();
            ecriture.setTransaction(transaction);
            ecriture.setCompte(compte);
            ecriture.setDebit(ligne.debit());
            ecriture.setCredit(ligne.credit());
            ecritureComptableRepository.save(ecriture);

            transaction.getEcritures().add(ecriture);

            // Mise a jour du solde du compte selon sa nature
            mettreAJourSolde(compte, ligne.debit(), ligne.credit());
            compteComptableRepository.save(compte);
        }

        return transaction;
    }

    /**
     * Valide que l'ecriture respecte le principe de la partie double.
     */
    private void validerEcritures(List<LigneEcritureDTO> lignes) {
        if (lignes == null || lignes.isEmpty()) {
            throw new IllegalArgumentException("L'ecriture comptable doit contenir au moins une ligne");
        }

        BigDecimal totalDebit = BigDecimal.ZERO;
        BigDecimal totalCredit = BigDecimal.ZERO;

        for (LigneEcritureDTO ligne : lignes) {
            BigDecimal debit = ligne.debit() != null ? ligne.debit() : BigDecimal.ZERO;
            BigDecimal credit = ligne.credit() != null ? ligne.credit() : BigDecimal.ZERO;

            // Chaque ligne doit avoir soit un debit, soit un credit, pas les deux
            if (debit.compareTo(BigDecimal.ZERO) > 0 && credit.compareTo(BigDecimal.ZERO) > 0) {
                throw new IllegalStateException(
                        "Une ligne d'ecriture ne peut pas avoir simultanement un debit et un credit " +
                        "(compte: " + ligne.numeroCompte() + ")");
            }
            if (debit.compareTo(BigDecimal.ZERO) <= 0 && credit.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalStateException(
                        "Une ligne d'ecriture doit avoir soit un debit soit un credit " +
                        "(compte: " + ligne.numeroCompte() + ")");
            }

            totalDebit = totalDebit.add(debit);
            totalCredit = totalCredit.add(credit);
        }

        // Verification de l'equilibre : total debit == total credit
        if (totalDebit.compareTo(totalCredit) != 0) {
            throw new IllegalStateException(
                    "L'ecriture comptable n'est pas equilibree : " +
                    "total debit (" + totalDebit + ") != total credit (" + totalCredit + ")");
        }
    }

    /**
     * Met a jour le solde d'un compte comptable selon sa nature (DEBITEUR ou CREDITEUR).
     * - Compte DEBITEUR : le debit augmente le solde, le credit le diminue
     * - Compte CREDITEUR : le credit augmente le solde, le debit le diminue
     */
    private void mettreAJourSolde(CompteComptable compte, BigDecimal debit, BigDecimal credit) {
        if ("DEBITEUR".equals(compte.getNatureSolde())) {
            compte.setSolde(compte.getSolde().add(debit).subtract(credit));
        } else {
            compte.setSolde(compte.getSolde().add(credit).subtract(debit));
        }
    }
}
