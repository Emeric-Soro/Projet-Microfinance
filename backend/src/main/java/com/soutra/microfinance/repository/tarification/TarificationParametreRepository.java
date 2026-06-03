package com.soutra.microfinance.repository.tarification;

import com.soutra.microfinance.entity.TarificationParametre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TarificationParametreRepository extends JpaRepository<TarificationParametre, Long> {

    Optional<TarificationParametre> findByCleParametre(String cleParametre);

    List<TarificationParametre> findAllByOrderByCleParametreAsc();
}
