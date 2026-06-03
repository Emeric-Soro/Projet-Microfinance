package com.soutra.microfinance.repository.conformite;

import com.soutra.microfinance.entity.conformite.ConsentementRgpd;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConsentementRgpdRepository extends JpaRepository<ConsentementRgpd, Long> {

    Optional<ConsentementRgpd> findByIdClientAndFinalite(Long idClient, String finalite);

    List<ConsentementRgpd> findByIdClient(Long idClient);
}
