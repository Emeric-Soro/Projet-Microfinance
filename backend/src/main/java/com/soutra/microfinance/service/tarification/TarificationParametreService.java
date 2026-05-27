package com.soutra.microfinance.service.tarification;

import java.math.BigDecimal;

public interface TarificationParametreService {

    BigDecimal lireValeurDecimale(String cleParametre);

    void invaliderCache();
}
