package com.soutra.microfinance.service.tarification;

import com.soutra.microfinance.entity.TarificationParametre;
import java.math.BigDecimal;
import java.util.List;

public interface TarificationParametreService {

    BigDecimal lireValeurDecimale(String cleParametre);

    void invaliderCache();

    List<TarificationParametre> listerTousParametres();

    TarificationParametre creerParametre(TarificationParametre parametre);
}
