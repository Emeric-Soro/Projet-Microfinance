package com.soutra.microfinance.repository.operation;

import com.soutra.microfinance.entity.Caisse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface CaisseRepository extends JpaRepository<Caisse, Long> {

    Optional<Caisse> findByUtilisateur_IdUserAndStatut(Long idUser, Caisse.StatutCaisse statut);

    long countByStatut(Caisse.StatutCaisse statut);

    @Query("SELECT COALESCE(SUM(c.soldeCourant), 0) FROM Caisse c WHERE c.statut = :statut")
    BigDecimal sumSoldeCourantByStatut(@Param("statut") Caisse.StatutCaisse statut);
}
