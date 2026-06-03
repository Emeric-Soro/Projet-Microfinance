package com.soutra.microfinance.service.compte;

import com.soutra.microfinance.entity.ReleveFormat;

import java.time.LocalDate;

public interface ReleveService {

    byte[] genererReleve(String numCompte, LocalDate du, LocalDate au, ReleveFormat format);
}
