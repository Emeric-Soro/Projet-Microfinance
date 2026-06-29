package com.soutra.microfinance.service.parametrage;

import com.soutra.microfinance.dto.request.parametrage.JourFerieRequestDTO;
import com.soutra.microfinance.dto.response.parametrage.JourFerieResponseDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ParametrageSystemeServiceImpl implements ParametrageSystemeService {

    private final Map<String, String> parametresSysteme = new ConcurrentHashMap<>(initParametres());
    private final List<JourFerieResponseDTO> joursFeries = new ArrayList<>(initJoursFeries());

    private static Map<String, String> initParametres() {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("DEVISE", "XOF");
        params.put("DEVISE_DEFAULT", "XOF");
        params.put("TIMEZONE", "Africa/Abidjan");
        params.put("SESSION_TIMEOUT_MIN", "15");
        params.put("SESSION_ALERT_MIN", "2");
        params.put("PAYS", "CI");
        params.put("INDICATIF_TELEPHONIQUE", "+225");
        params.put("SEUIL_TAEG_MAX", "24.0");
        params.put("SEUIL_4_YEUX", "500000");
        params.put("DELAI_INACTIVITE_MINUTES", "15");
        params.put("NOM_INSTITUTION", "SOUTRA MICROFINANCE");
        params.put("SLOGAN", "Votre Soutien Financier Durable");
        params.put("SERVICE_CLIENT", "1300");
        return params;
    }

    private static List<JourFerieResponseDTO> initJoursFeries() {
        List<JourFerieResponseDTO> jours = new ArrayList<>();
        jours.add(jourFerie("Jour de l'An", "2025-01-01", true, "CI"));
        jours.add(jourFerie("Fete du Travail", "2025-05-01", true, "CI"));
        jours.add(jourFerie("Fete Nationale", "2025-08-07", true, "CI"));
        jours.add(jourFerie("Assomption", "2025-08-15", true, "CI"));
        jours.add(jourFerie("Paix", "2025-11-15", true, "CI"));
        jours.add(jourFerie("Noel", "2025-12-25", true, "CI"));
        return jours;
    }

    private static JourFerieResponseDTO jourFerie(String nom, String dateJour, boolean recurrent, String pays) {
        return new JourFerieResponseDTO((long) nom.hashCode(), nom, dateJour, recurrent, pays);
    }

    @Override
    public Map<String, String> consulterParametres() {
        return new LinkedHashMap<>(parametresSysteme);
    }

    @Override
    public Map<String, String> mettreAJourParametre(String code, String valeur) {
        if (parametresSysteme.containsKey(code)) {
            parametresSysteme.put(code, valeur);
        } else {
            parametresSysteme.put(code, valeur);
        }
        return new LinkedHashMap<>(parametresSysteme);
    }

    @Override
    public List<JourFerieResponseDTO> listerJoursFeries() {
        return new ArrayList<>(joursFeries);
    }

    @Override
    public List<JourFerieResponseDTO> mettreAJourJoursFeries(List<JourFerieRequestDTO> nouveauxJours) {
        joursFeries.clear();
        for (JourFerieRequestDTO jour : nouveauxJours) {
            String pays = jour.getPays() == null || jour.getPays().isBlank() ? "CI" : jour.getPays();
            Boolean recurrent = jour.getRecurrent() == null ? Boolean.TRUE : jour.getRecurrent();
            joursFeries.add(jourFerie(jour.getNom(), jour.getDateJour(), recurrent, pays));
        }
        return new ArrayList<>(joursFeries);
    }
}
