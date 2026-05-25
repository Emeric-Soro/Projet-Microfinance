package com.soutra.microfinance.repository.comptabilite;

import com.soutra.microfinance.entity.comptabilite.EcritureComptable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface EcritureComptableRepository extends JpaRepository<EcritureComptable, Long> {

    // Recherche des ecritures d'une transaction comptable.
    List<EcritureComptable> findByTransaction_Id(Long transactionId);

    // Recherche des ecritures impactant un compte specifique.
    List<EcritureComptable> findByCompte_NumeroCompte(String numeroCompte);

    // Calcul du solde net (debit - credit) d'un compte dans le grand livre.
    @Query("SELECT COALESCE(SUM(e.debit), 0) - COALESCE(SUM(e.credit), 0) FROM EcritureComptable e WHERE e.compte.numeroCompte = :numeroCompte")
    BigDecimal calculerSoldeCompte(@Param("numeroCompte") String numeroCompte);
}
