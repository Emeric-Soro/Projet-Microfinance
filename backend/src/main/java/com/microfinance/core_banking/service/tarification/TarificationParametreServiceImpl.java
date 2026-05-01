package com.microfinance.core_banking.service.tarification;

import com.microfinance.core_banking.entity.TarificationParametre;
import com.microfinance.core_banking.repository.tarification.TarificationParametreRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class TarificationParametreServiceImpl implements TarificationParametreService {

    private final TarificationParametreRepository tarificationParametreRepository;

    public TarificationParametreServiceImpl(TarificationParametreRepository tarificationParametreRepository) {
        this.tarificationParametreRepository = tarificationParametreRepository;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "tarification-parametres", key = "#root.args[0]")
    public BigDecimal lireValeurDecimale(String cleParametre) {
        if (cleParametre == null || cleParametre.isBlank()) {
            throw new IllegalArgumentException("La cle de parametre est obligatoire");
        }

        TarificationParametre parametre = tarificationParametreRepository.findByCleParametre(cleParametre)
                .orElseThrow(() -> new IllegalStateException("Parametre de tarification introuvable: " + cleParametre));

        try {
            return new BigDecimal(parametre.getValeurParametre());
        } catch (NumberFormatException ex) {
            throw new IllegalStateException("Valeur non numerique pour le parametre: " + cleParametre);
        }
    }

    @Override
    @CacheEvict(cacheNames = "tarification-parametres", allEntries = true)
    public void invaliderCache() {
        // Invalidation explicite, utile apres mise a jour des parametres en base.
    }
}
