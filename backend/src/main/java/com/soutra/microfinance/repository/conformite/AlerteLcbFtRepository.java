package com.soutra.microfinance.repository.conformite;

import com.soutra.microfinance.entity.conformite.AlerteLcbFt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlerteLcbFtRepository extends JpaRepository<AlerteLcbFt, Long> {

    List<AlerteLcbFt> findByStatut(String statut);

    List<AlerteLcbFt> findByNiveauRisque(String niveauRisque);

    List<AlerteLcbFt> findByIdClient(Long idClient);

    Page<AlerteLcbFt> findAll(Pageable pageable);
}
