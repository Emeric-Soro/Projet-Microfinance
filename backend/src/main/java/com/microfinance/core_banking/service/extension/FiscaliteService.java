package com.microfinance.core_banking.service.extension;

import com.microfinance.core_banking.entity.Agence;
import com.microfinance.core_banking.entity.DeclarationFiscale;
import com.microfinance.core_banking.entity.TauxFiscal;
import com.microfinance.core_banking.entity.Transaction;
import com.microfinance.core_banking.repository.extension.AgenceRepository;
import com.microfinance.core_banking.repository.extension.DeclarationFiscaleRepository;
import com.microfinance.core_banking.repository.extension.TauxFiscalRepository;
import com.microfinance.core_banking.repository.operation.TransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class FiscaliteService {

    private final TauxFiscalRepository tauxFiscalRepository;
    private final DeclarationFiscaleRepository declarationFiscaleRepository;
    private final TransactionRepository transactionRepository;
    private final AgenceRepository agenceRepository;

    public FiscaliteService(TauxFiscalRepository tauxFiscalRepository,
                            DeclarationFiscaleRepository declarationFiscaleRepository,
                            TransactionRepository transactionRepository,
                            AgenceRepository agenceRepository) {
        this.tauxFiscalRepository = tauxFiscalRepository;
        this.declarationFiscaleRepository = declarationFiscaleRepository;
        this.transactionRepository = transactionRepository;
        this.agenceRepository = agenceRepository;
    }

    public BigDecimal calculerTaxe(String codeTaxe, BigDecimal montantOperation, String typeOperation) {
        TauxFiscal taux = tauxFiscalRepository.findByCodeTaxeAndActifTrue(codeTaxe)
                .orElse(null);
        if (taux == null) return BigDecimal.ZERO;

        if (taux.getSeuilApplicable() != null
                && montantOperation.compareTo(taux.getSeuilApplicable()) < 0) {
            return BigDecimal.ZERO;
        }

        if (taux.getTypeOperation() != null
                && !taux.getTypeOperation().equalsIgnoreCase(typeOperation)
                && !"TOUTES".equalsIgnoreCase(taux.getTypeOperation())) {
            return BigDecimal.ZERO;
        }

        return montantOperation.multiply(taux.getTaux())
                .setScale(2, RoundingMode.HALF_UP);
    }

    @Transactional
    public DeclarationFiscale genererDeclaration(String typeDeclaration, LocalDate periodeDebut,
                                                  LocalDate periodeFin, Long agenceId) {
        Agence agence = null;
        if (agenceId != null) {
            agence = agenceRepository.findById(agenceId)
                    .orElseThrow(() -> new EntityNotFoundException("Agence introuvable: " + agenceId));
        }

        List<Transaction> transactions = transactionRepository.findByDateExecutionBetween(periodeDebut.atStartOfDay(), periodeFin.atTime(23, 59, 59));
        if (agenceId != null) {
            transactions = transactions.stream()
                    .filter(t -> t.getAgenceOperation() != null && agenceId.equals(t.getAgenceOperation().getIdAgence()))
                    .toList();
        }

        BigDecimal montantBase = transactions.stream()
                .map(t -> t.getFrais() != null ? t.getFrais() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal taxePayable = BigDecimal.ZERO;
        List<TauxFiscal> tauxActifs = tauxFiscalRepository.findByActifTrue();
        for (TauxFiscal taux : tauxActifs) {
            BigDecimal taxeTranche = BigDecimal.ZERO;
            if (taux.getSeuilApplicable() == null || montantBase.compareTo(taux.getSeuilApplicable()) >= 0) {
                taxeTranche = montantBase.multiply(taux.getTaux()).setScale(2, RoundingMode.HALF_UP);
            }
            taxePayable = taxePayable.add(taxeTranche);
        }

        DeclarationFiscale declaration = new DeclarationFiscale();
        declaration.setReferenceDeclaration("DEC-" + typeDeclaration + "-" + periodeDebut + "-" + UUID.randomUUID().toString().substring(0, 8));
        declaration.setTypeDeclaration(typeDeclaration);
        declaration.setPeriodeDebut(periodeDebut);
        declaration.setPeriodeFin(periodeFin);
        declaration.setMontantBase(montantBase);
        declaration.setMontantTaxe(taxePayable);
        declaration.setStatut("BROUILLON");
        declaration.setAgence(agence);

        return declarationFiscaleRepository.save(declaration);
    }

    @Transactional
    public DeclarationFiscale validerDeclaration(Long idDeclaration) {
        DeclarationFiscale declaration = declarationFiscaleRepository.findById(idDeclaration)
                .orElseThrow(() -> new EntityNotFoundException("Declaration introuvable: " + idDeclaration));
        declaration.setStatut("VALIDEE");
        declaration.setDateDeclaration(LocalDate.now());
        return declarationFiscaleRepository.save(declaration);
    }

    @Transactional(readOnly = true)
    public List<DeclarationFiscale> listerDeclarations(String type, LocalDate debut, LocalDate fin) {
        return declarationFiscaleRepository
                .findByTypeDeclarationAndPeriodeDebutGreaterThanEqualAndPeriodeFinLessThanEqual(type, debut, fin);
    }
}
