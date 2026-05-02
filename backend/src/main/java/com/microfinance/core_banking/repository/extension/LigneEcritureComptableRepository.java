package com.microfinance.core_banking.repository.extension;

import com.microfinance.core_banking.entity.LigneEcritureComptable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface LigneEcritureComptableRepository extends JpaRepository<LigneEcritureComptable, Long> {
    List<LigneEcritureComptable> findByCompteComptable_NumeroCompteAndEcritureComptable_DateComptableBetweenOrderByEcritureComptable_DateComptableAsc(
            String numeroCompte,
            LocalDate dateDebut,
            LocalDate dateFin
    );

    List<LigneEcritureComptable> findByEcritureComptable_DateComptableBetween(LocalDate dateDebut, LocalDate dateFin);

    List<LigneEcritureComptable> findByReferenceAuxiliaire(String referenceAuxiliaire);

    List<LigneEcritureComptable> findByEcritureComptable_IdEcritureComptable(Long idEcritureComptable);
}
