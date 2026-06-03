package com.soutra.microfinance.repository.conformite;

import com.soutra.microfinance.entity.conformite.PersonnePolitiquementExposee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonnePolitiquementExposeeRepository extends JpaRepository<PersonnePolitiquementExposee, Long> {

    Optional<PersonnePolitiquementExposee> findByIdClient(Long idClient);

    List<PersonnePolitiquementExposee> findByStatut(String statut);

    List<PersonnePolitiquementExposee> findByNiveauRisque(String niveauRisque);

    Page<PersonnePolitiquementExposee> findAll(Pageable pageable);
}
