package com.microfinance.core_banking.repository.operation;

import com.microfinance.core_banking.entity.Caisse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CaisseRepository extends JpaRepository<Caisse, Long> {

    Optional<Caisse> findByUtilisateur_IdUserAndStatut(Long idUser, Caisse.StatutCaisse statut);
}
