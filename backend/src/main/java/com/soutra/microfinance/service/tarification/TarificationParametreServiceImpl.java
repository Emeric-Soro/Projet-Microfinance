package com.soutra.microfinance.service.tarification;

import com.soutra.microfinance.entity.TarificationParametre;
import com.soutra.microfinance.repository.tarification.TarificationParametreRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

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

    @Override
    @Transactional(readOnly = true)
    public List<TarificationParametre> listerTousParametres() {
        return tarificationParametreRepository.findAllByOrderByCleParametreAsc();
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "tarification-parametres", allEntries = true)
    public TarificationParametre creerParametre(TarificationParametre parametre) {
        if (tarificationParametreRepository.findByCleParametre(parametre.getCleParametre()).isPresent()) {
            throw new IllegalArgumentException("Le parametre '" + parametre.getCleParametre() + "' existe deja.");
        }
        return tarificationParametreRepository.save(parametre);
    }
}
