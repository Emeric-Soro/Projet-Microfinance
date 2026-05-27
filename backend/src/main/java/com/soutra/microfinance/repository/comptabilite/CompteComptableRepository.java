package com.soutra.microfinance.repository.comptabilite;

import com.soutra.microfinance.entity.comptabilite.CompteComptable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompteComptableRepository extends JpaRepository<CompteComptable, String> {

    // Recherche des comptes par classe SYSCOHADA (CLASSE_1 a CLASSE_7).
    List<CompteComptable> findByClasse(String classe);

    // Recherche des comptes actifs uniquement.
    List<CompteComptable> findByActifSn(String actifSn);

    // Verification de l'existence d'un compte par numero.
    boolean existsByNumeroCompte(String numeroCompte);
}
