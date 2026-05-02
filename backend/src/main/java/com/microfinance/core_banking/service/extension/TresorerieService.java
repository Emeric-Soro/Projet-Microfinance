package com.microfinance.core_banking.service.extension;

import com.microfinance.core_banking.dto.request.extension.ApprovisionnerCaisseServiceRequestDTO;
import com.microfinance.core_banking.dto.request.extension.CreerCaisseServiceRequestDTO;
import com.microfinance.core_banking.dto.request.extension.CreerCoffreServiceRequestDTO;
import com.microfinance.core_banking.dto.request.extension.DelesterCaisseServiceRequestDTO;
import com.microfinance.core_banking.dto.request.extension.FermerSessionServiceRequestDTO;
import com.microfinance.core_banking.dto.request.extension.OuvrirSessionServiceRequestDTO;
import com.microfinance.core_banking.entity.Agence;
import com.microfinance.core_banking.entity.ApprovisionnementCaisse;
import com.microfinance.core_banking.entity.Caisse;
import com.microfinance.core_banking.entity.Coffre;
import com.microfinance.core_banking.entity.DelestageCaisse;
import com.microfinance.core_banking.entity.Guichet;
import com.microfinance.core_banking.entity.MouvementCoffre;
import com.microfinance.core_banking.entity.SessionCaisse;
import com.microfinance.core_banking.entity.StatutOperation;
import com.microfinance.core_banking.entity.Utilisateur;
import com.microfinance.core_banking.repository.extension.ApprovisionnementCaisseRepository;
import com.microfinance.core_banking.repository.client.UtilisateurRepository;
import com.microfinance.core_banking.repository.extension.AgenceRepository;
import com.microfinance.core_banking.repository.extension.CaisseRepository;
import com.microfinance.core_banking.repository.extension.CoffreRepository;
import com.microfinance.core_banking.repository.extension.DelestageCaisseRepository;
import com.microfinance.core_banking.repository.extension.GuichetRepository;
import com.microfinance.core_banking.repository.extension.MouvementCoffreRepository;
import com.microfinance.core_banking.repository.extension.SessionCaisseRepository;
import com.microfinance.core_banking.repository.operation.TransactionRepository;
import com.microfinance.core_banking.service.security.AuthenticatedUserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TresorerieService {

    private final CaisseRepository caisseRepository;
    private final CoffreRepository coffreRepository;
    private final MouvementCoffreRepository mouvementCoffreRepository;
    private final ApprovisionnementCaisseRepository approvisionnementCaisseRepository;
    private final DelestageCaisseRepository delestageCaisseRepository;
    private final SessionCaisseRepository sessionCaisseRepository;
    private final AgenceRepository agenceRepository;
    private final GuichetRepository guichetRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final TransactionRepository transactionRepository;
    private final AuthenticatedUserService authenticatedUserService;
    private final ComptabiliteExtensionService comptabiliteExtensionService;

    public TresorerieService(
            CaisseRepository caisseRepository,
            CoffreRepository coffreRepository,
            MouvementCoffreRepository mouvementCoffreRepository,
            ApprovisionnementCaisseRepository approvisionnementCaisseRepository,
            DelestageCaisseRepository delestageCaisseRepository,
            SessionCaisseRepository sessionCaisseRepository,
            AgenceRepository agenceRepository,
            GuichetRepository guichetRepository,
            UtilisateurRepository utilisateurRepository,
            TransactionRepository transactionRepository,
            AuthenticatedUserService authenticatedUserService,
            ComptabiliteExtensionService comptabiliteExtensionService
    ) {
        this.caisseRepository = caisseRepository;
        this.coffreRepository = coffreRepository;
        this.mouvementCoffreRepository = mouvementCoffreRepository;
        this.approvisionnementCaisseRepository = approvisionnementCaisseRepository;
        this.delestageCaisseRepository = delestageCaisseRepository;
        this.sessionCaisseRepository = sessionCaisseRepository;
        this.agenceRepository = agenceRepository;
        this.guichetRepository = guichetRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.transactionRepository = transactionRepository;
        this.authenticatedUserService = authenticatedUserService;
        this.comptabiliteExtensionService = comptabiliteExtensionService;
    }

    @Transactional
    public Caisse creerCaisse(CreerCaisseServiceRequestDTO dto) {
        Agence agence = agenceRepository.findById(Long.valueOf(dto.getIdAgence()))
                .orElseThrow(() -> new EntityNotFoundException("Agence introuvable"));
        authenticatedUserService.assertAgencyAccess(agence.getIdAgence());

        Caisse caisse = new Caisse();
        caisse.setCodeCaisse(dto.getCodeCaisse());
        caisse.setLibelle(dto.getLibelle());
        caisse.setAgence(agence);
        caisse.setStatut(dto.getStatut());
        caisse.setSoldeTheorique(dto.getSoldeTheorique());
        if (dto.getIdGuichet() != null) {
            Guichet guichet = guichetRepository.findById(Long.valueOf(dto.getIdGuichet()))
                    .orElseThrow(() -> new EntityNotFoundException("Guichet introuvable"));
            caisse.setGuichet(guichet);
        }
        return caisseRepository.save(caisse);
    }

    @Transactional
    public Coffre creerCoffre(CreerCoffreServiceRequestDTO dto) {
        Agence agence = agenceRepository.findById(Long.valueOf(dto.getIdAgence()))
                .orElseThrow(() -> new EntityNotFoundException("Agence introuvable"));
        authenticatedUserService.assertAgencyAccess(agence.getIdAgence());

        Coffre coffre = new Coffre();
        coffre.setCodeCoffre(dto.getCodeCoffre());
        coffre.setLibelle(dto.getLibelle());
        coffre.setAgence(agence);
        coffre.setStatut(dto.getStatut());
        coffre.setSoldeTheorique(dto.getSoldeTheorique());
        return coffreRepository.save(coffre);
    }

    @Transactional
    public SessionCaisse ouvrirSession(OuvrirSessionServiceRequestDTO dto) {
        Caisse caisse = caisseRepository.findById(Long.valueOf(dto.getIdCaisse()))
                .orElseThrow(() -> new EntityNotFoundException("Caisse introuvable"));
        Utilisateur utilisateur = utilisateurRepository.findById(Long.valueOf(dto.getIdUtilisateur()))
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable"));
        authenticatedUserService.assertAgencyAccess(caisse.getAgence().getIdAgence());

        SessionCaisse session = new SessionCaisse();
        session.setCaisse(caisse);
        session.setUtilisateur(utilisateur);
        session.setDateOuverture(LocalDateTime.now());
        session.setSoldeOuverture(dto.getSoldeOuverture());
        session.setSoldeTheoriqueFermeture(session.getSoldeOuverture());
        session.setStatut("OUVERTE");
        session = sessionCaisseRepository.save(session);

        comptabiliteExtensionService.comptabiliserMouvementTresorerie(
                "OUVERTURE_CAISSE",
                "SESSION-OPEN-" + session.getIdSessionCaisse(),
                "Ouverture session caisse " + caisse.getCodeCaisse() + " - solde: " + dto.getSoldeOuverture(),
                dto.getSoldeOuverture(),
                caisse.getCodeCaisse(),
                caisse.getLibelle(),
                null,
                null
        );
        return session;
    }

    @Transactional
    public SessionCaisse fermerSession(Long idSession, FermerSessionServiceRequestDTO dto) {
        SessionCaisse session = sessionCaisseRepository.findById(idSession)
                .orElseThrow(() -> new EntityNotFoundException("Session de caisse introuvable"));
        authenticatedUserService.assertAgencyAccess(session.getCaisse().getAgence().getIdAgence());
        if (transactionRepository.countBySessionCaisse_IdSessionCaisseAndStatutOperation(idSession, StatutOperation.EN_ATTENTE) > 0) {
            throw new IllegalStateException("Impossible de fermer la session tant que des operations cash sont en attente");
        }

        BigDecimal soldePhysique = dto.getSoldePhysiqueFermeture();
        session.setDateFermeture(LocalDateTime.now());
        session.setSoldePhysiqueFermeture(soldePhysique);
        session.setSoldeTheoriqueFermeture(dto.getSoldeTheoriqueFermeture() == null
                ? session.getSoldeTheoriqueFermeture()
                : dto.getSoldeTheoriqueFermeture());
        session.setEcart(soldePhysique.subtract(session.getSoldeTheoriqueFermeture()));
        session.setCommentaire(dto.getCommentaire());
        if (session.getEcart() != null
                && session.getEcart().compareTo(BigDecimal.ZERO) != 0
                && (session.getCommentaire() == null || session.getCommentaire().isBlank())) {
            throw new IllegalStateException("Tout ecart de caisse doit etre justifie avant la fermeture");
        }
        session.setStatut("FERMEE");

        Caisse caisse = session.getCaisse();
        caisse.setSoldeTheorique(session.getSoldeTheoriqueFermeture());
        caisseRepository.save(caisse);

        comptabiliteExtensionService.comptabiliserMouvementTresorerie(
                "FERMETURE_CAISSE",
                "SESSION-CLOSE-" + session.getIdSessionCaisse(),
                "Fermeture session caisse " + caisse.getCodeCaisse() + " - solde theorique: " + session.getSoldeTheoriqueFermeture(),
                session.getSoldeTheoriqueFermeture(),
                caisse.getCodeCaisse(),
                caisse.getLibelle(),
                null,
                null
        );

        if (session.getEcart() != null && session.getEcart().compareTo(BigDecimal.ZERO) != 0) {
            String codeOperation = session.getEcart().compareTo(BigDecimal.ZERO) > 0
                    ? "ECART_CAISSE_EXCEDENT"
                    : "ECART_CAISSE_DEFICIT";
            String libelle = session.getEcart().compareTo(BigDecimal.ZERO) > 0
                    ? "Excedent de caisse session " + caisse.getCodeCaisse() + " - montant: " + session.getEcart().abs()
                    : "Deficit de caisse session " + caisse.getCodeCaisse() + " - montant: " + session.getEcart().abs();
            comptabiliteExtensionService.comptabiliserMouvementTresorerie(
                    codeOperation,
                    "SESSION-GAP-" + session.getIdSessionCaisse(),
                    libelle,
                    session.getEcart().abs(),
                    caisse.getCodeCaisse(),
                    caisse.getLibelle(),
                    null,
                    null
            );
        }

        return sessionCaisseRepository.save(session);
    }

    @Transactional
    public ApprovisionnementCaisse approvisionnerCaisse(ApprovisionnerCaisseServiceRequestDTO dto) {
        Coffre coffre = coffreRepository.findById(Long.valueOf(dto.getIdCoffre()))
                .orElseThrow(() -> new EntityNotFoundException("Coffre introuvable"));
        Caisse caisse = caisseRepository.findById(Long.valueOf(dto.getIdCaisse()))
                .orElseThrow(() -> new EntityNotFoundException("Caisse introuvable"));
        verifierMemeAgence(coffre, caisse);
        BigDecimal montant = dto.getMontant();
        if (coffre.getSoldeTheorique().compareTo(montant) < 0) {
            throw new IllegalStateException("Solde coffre insuffisant pour approvisionner la caisse");
        }

        ApprovisionnementCaisse approvisionnement = new ApprovisionnementCaisse();
        approvisionnement.setCoffre(coffre);
        approvisionnement.setCaisse(caisse);
        approvisionnement.setMontant(montant);
        approvisionnement.setReferenceOperation(dto.getReferenceOperation() == null ? "APP-" + randomSuffix() : dto.getReferenceOperation());

        coffre.setSoldeTheorique(coffre.getSoldeTheorique().subtract(montant));
        caisse.setSoldeTheorique(caisse.getSoldeTheorique().add(montant));
        mettreAJourSessionOuverte(caisse, montant);

        MouvementCoffre mouvementCoffre = new MouvementCoffre();
        mouvementCoffre.setCoffre(coffre);
        mouvementCoffre.setTypeMouvement("APPROVISIONNEMENT_CAISSE");
        mouvementCoffre.setMontant(montant);
        mouvementCoffre.setReferenceMouvement(approvisionnement.getReferenceOperation());
        mouvementCoffre.setCommentaire(dto.getCommentaire());

        coffreRepository.save(coffre);
        caisseRepository.save(caisse);
        mouvementCoffreRepository.save(mouvementCoffre);
        comptabiliteExtensionService.comptabiliserMouvementTresorerie(
                "APPROVISIONNEMENT_CAISSE",
                approvisionnement.getReferenceOperation(),
                "Approvisionnement caisse " + caisse.getCodeCaisse(),
                montant,
                caisse.getCodeCaisse(),
                caisse.getLibelle(),
                coffre.getCodeCoffre(),
                coffre.getLibelle()
        );
        return approvisionnementCaisseRepository.save(approvisionnement);
    }

    @Transactional
    public DelestageCaisse delesterCaisse(DelesterCaisseServiceRequestDTO dto) {
        Coffre coffre = coffreRepository.findById(Long.valueOf(dto.getIdCoffre()))
                .orElseThrow(() -> new EntityNotFoundException("Coffre introuvable"));
        Caisse caisse = caisseRepository.findById(Long.valueOf(dto.getIdCaisse()))
                .orElseThrow(() -> new EntityNotFoundException("Caisse introuvable"));
        verifierMemeAgence(coffre, caisse);
        BigDecimal montant = dto.getMontant();
        if (caisse.getSoldeTheorique().compareTo(montant) < 0) {
            throw new IllegalStateException("Solde caisse insuffisant pour le delestage");
        }

        DelestageCaisse delestage = new DelestageCaisse();
        delestage.setCoffre(coffre);
        delestage.setCaisse(caisse);
        delestage.setMontant(montant);
        delestage.setReferenceOperation(dto.getReferenceOperation() == null ? "DEL-" + randomSuffix() : dto.getReferenceOperation());

        caisse.setSoldeTheorique(caisse.getSoldeTheorique().subtract(montant));
        coffre.setSoldeTheorique(coffre.getSoldeTheorique().add(montant));
        mettreAJourSessionOuverte(caisse, montant.negate());

        MouvementCoffre mouvementCoffre = new MouvementCoffre();
        mouvementCoffre.setCoffre(coffre);
        mouvementCoffre.setTypeMouvement("DELESTAGE_CAISSE");
        mouvementCoffre.setMontant(montant);
        mouvementCoffre.setReferenceMouvement(delestage.getReferenceOperation());
        mouvementCoffre.setCommentaire(dto.getCommentaire());

        caisseRepository.save(caisse);
        coffreRepository.save(coffre);
        mouvementCoffreRepository.save(mouvementCoffre);
        comptabiliteExtensionService.comptabiliserMouvementTresorerie(
                "DELESTAGE_CAISSE",
                delestage.getReferenceOperation(),
                "Delestage caisse " + caisse.getCodeCaisse(),
                montant,
                coffre.getCodeCoffre(),
                coffre.getLibelle(),
                caisse.getCodeCaisse(),
                caisse.getLibelle()
        );
        return delestageCaisseRepository.save(delestage);
    }

    @Transactional(readOnly = true)
    public List<Caisse> listerCaisses() {
        return caisseRepository.findAll().stream()
                .filter(caisse -> authenticatedUserService.hasGlobalScope()
                        || (authenticatedUserService.getCurrentAgencyId() != null
                        && caisse.getAgence() != null
                        && authenticatedUserService.getCurrentAgencyId().equals(caisse.getAgence().getIdAgence())))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SessionCaisse> listerSessions() {
        return sessionCaisseRepository.findAll().stream()
                .filter(session -> authenticatedUserService.hasGlobalScope()
                        || (authenticatedUserService.getCurrentAgencyId() != null
                        && session.getCaisse() != null
                        && session.getCaisse().getAgence() != null
                        && authenticatedUserService.getCurrentAgencyId().equals(session.getCaisse().getAgence().getIdAgence())))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Coffre> listerCoffres() {
        return coffreRepository.findAll().stream()
                .filter(coffre -> authenticatedUserService.hasGlobalScope()
                        || (authenticatedUserService.getCurrentAgencyId() != null
                        && coffre.getAgence() != null
                        && authenticatedUserService.getCurrentAgencyId().equals(coffre.getAgence().getIdAgence())))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MouvementCoffre> listerMouvementsCoffre(Long idCoffre) {
        Coffre coffre = coffreRepository.findById(idCoffre)
                .orElseThrow(() -> new EntityNotFoundException("Coffre introuvable"));
        authenticatedUserService.assertAgencyAccess(coffre.getAgence().getIdAgence());
        return mouvementCoffreRepository.findByCoffre_IdCoffreOrderByCreatedAtDesc(idCoffre);
    }

    private void verifierMemeAgence(Coffre coffre, Caisse caisse) {
        if (coffre.getAgence() == null || caisse.getAgence() == null || !coffre.getAgence().getIdAgence().equals(caisse.getAgence().getIdAgence())) {
            throw new IllegalStateException("Le coffre et la caisse doivent appartenir a la meme agence");
        }
        authenticatedUserService.assertAgencyAccess(coffre.getAgence().getIdAgence());
    }

    private void mettreAJourSessionOuverte(Caisse caisse, BigDecimal variation) {
        SessionCaisse session = sessionCaisseRepository.findFirstByCaisse_IdCaisseAndStatutIgnoreCaseOrderByDateOuvertureDesc(caisse.getIdCaisse(), "OUVERTE");
        if (session == null) {
            return;
        }
        session.setSoldeTheoriqueFermeture(session.getSoldeTheoriqueFermeture().add(variation));
        sessionCaisseRepository.save(session);
    }

    private String randomSuffix() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }
}
