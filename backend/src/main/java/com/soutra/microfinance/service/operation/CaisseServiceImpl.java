package com.soutra.microfinance.service.operation;

import com.soutra.microfinance.dto.request.operation.FermetureCaisseRequestDTO;
import com.soutra.microfinance.dto.request.operation.OuvertureCaisseRequestDTO;
import com.soutra.microfinance.entity.Caisse;
import com.soutra.microfinance.entity.Caisse.StatutCaisse;
import com.soutra.microfinance.entity.Utilisateur;
import com.soutra.microfinance.repository.client.UtilisateurRepository;
import com.soutra.microfinance.repository.operation.CaisseRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.soutra.microfinance.audit.AuditContext;

@Service
public class CaisseServiceImpl implements CaisseService {

    private final CaisseRepository caisseRepository;
    private final UtilisateurRepository utilisateurRepository;

    public CaisseServiceImpl(CaisseRepository caisseRepository, UtilisateurRepository utilisateurRepository) {
        this.caisseRepository = caisseRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    @Override
    @Transactional
    public Caisse ouvrirCaisse(Long idUser, OuvertureCaisseRequestDTO dto) {
        Utilisateur utilisateur = utilisateurRepository.findById(idUser)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable : " + idUser));

        caisseRepository.findByUtilisateur_IdUserAndStatut(idUser, StatutCaisse.OUVERTE)
                .ifPresent(c -> {
                    throw new IllegalStateException("Vous avez deja une caisse ouverte (solde : " + c.getSoldeCourant() + " FCFA). Fermez-la avant d'en ouvrir une nouvelle.");
                });

        Caisse caisse = new Caisse();
        caisse.setUtilisateur(utilisateur);
        caisse.setSoldeOuverture(dto.getSoldeInitial());
        caisse.setSoldeCourant(dto.getSoldeInitial());
        caisse.setStatut(StatutCaisse.OUVERTE);
        caisse.setDateOuverture(LocalDateTime.now());
        Caisse saved = caisseRepository.save(caisse);

        AuditContext.setIdEntite(String.valueOf(saved.getIdCaisse()));
        java.util.Map<String, Object> avant = new java.util.HashMap<>();
        AuditContext.setDetailsAvant(AuditContext.toJson(avant));

        java.util.Map<String, Object> apres = new java.util.HashMap<>();
        apres.put("soldeOuverture", saved.getSoldeOuverture());
        apres.put("statut", StatutCaisse.OUVERTE);
        AuditContext.setDetailsApres(AuditContext.toJson(apres));

        return saved;
    }

    @Override
    @Transactional
    public Caisse fermerCaisse(Long idUser, FermetureCaisseRequestDTO dto) {
        Caisse caisse = caisseRepository.findByUtilisateur_IdUserAndStatut(idUser, StatutCaisse.OUVERTE)
                .orElseThrow(() -> new IllegalStateException("Aucune caisse ouverte a fermer."));

        BigDecimal soldeConstate = dto.getSoldePhysiqueConstate();
        BigDecimal ecart = soldeConstate.subtract(caisse.getSoldeCourant());

        AuditContext.setIdEntite(String.valueOf(caisse.getIdCaisse()));
        java.util.Map<String, Object> avant = new java.util.HashMap<>();
        avant.put("soldeCourant", caisse.getSoldeCourant());
        avant.put("statut", StatutCaisse.OUVERTE);
        AuditContext.setDetailsAvant(AuditContext.toJson(avant));

        caisse.setStatut(StatutCaisse.FERMEE);
        caisse.setDateFermeture(LocalDateTime.now());
        caisse.setEcartFermeture(ecart);
        Caisse saved = caisseRepository.save(caisse);

        java.util.Map<String, Object> apres = new java.util.HashMap<>();
        apres.put("soldePhysiqueConstate", soldeConstate);
        apres.put("statut", StatutCaisse.FERMEE);
        apres.put("ecartFermeture", ecart);
        AuditContext.setDetailsApres(AuditContext.toJson(apres));

        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public Caisse consulterCaisseOuverte(Long idUser) {
        return caisseRepository.findByUtilisateur_IdUserAndStatut(idUser, StatutCaisse.OUVERTE)
                .orElseThrow(() -> new IllegalStateException("Aucune caisse ouverte. Ouvrez une caisse d'abord."));
    }
}
