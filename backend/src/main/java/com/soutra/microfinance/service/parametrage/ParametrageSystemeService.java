package com.soutra.microfinance.service.parametrage;

import com.soutra.microfinance.dto.request.parametrage.JourFerieRequestDTO;
import com.soutra.microfinance.dto.response.parametrage.JourFerieResponseDTO;

import java.util.List;
import java.util.Map;

public interface ParametrageSystemeService {

    Map<String, String> consulterParametres();

    Map<String, String> mettreAJourParametre(String code, String valeur);

    List<JourFerieResponseDTO> listerJoursFeries();

    List<JourFerieResponseDTO> mettreAJourJoursFeries(List<JourFerieRequestDTO> joursFeries);
}
