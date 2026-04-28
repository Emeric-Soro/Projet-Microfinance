package com.microfinance.core_banking.repository.tarification;

import com.microfinance.core_banking.entity.TarificationParametre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TarificationParametreRepository extends JpaRepository<TarificationParametre, Long> {

    Optional<TarificationParametre> findByCleParametre(String cleParametre);
}
