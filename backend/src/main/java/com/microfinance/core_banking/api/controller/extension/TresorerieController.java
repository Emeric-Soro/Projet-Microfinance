package com.microfinance.core_banking.api.controller.extension;

import com.microfinance.core_banking.audit.AuditLog;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.entity.ApprovisionnementCaisse;
import com.microfinance.core_banking.entity.Caisse;
import com.microfinance.core_banking.entity.Coffre;
import com.microfinance.core_banking.entity.DelestageCaisse;
import com.microfinance.core_banking.entity.MouvementCoffre;
import com.microfinance.core_banking.entity.SessionCaisse;
import com.microfinance.core_banking.service.extension.PendingActionSubmissionService;
import com.microfinance.core_banking.service.extension.TresorerieService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tresorerie")
public class TresorerieController {

    private final TresorerieService tresorerieService;
    private final PendingActionSubmissionService pendingActionSubmissionService;

    public TresorerieController(TresorerieService tresorerieService, PendingActionSubmissionService pendingActionSubmissionService) {
        this.tresorerieService = tresorerieService;
        this.pendingActionSubmissionService = pendingActionSubmissionService;
    }

    @PostMapping("/caisses")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','TREASURY_MANAGE')")
    @AuditLog(action = "CAISSE_CREATE", resource = "CAISSE")
    public ResponseEntity<Map<String, Object>> creerCaisse(@RequestBody Map<String, Object> payload) {
        ActionEnAttente action = pendingActionSubmissionService.submit("CREATE_CAISSE", "CAISSE", (String) payload.get("codeCaisse"), payload, "Creation caisse soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionMap(action));
    }

    @GetMapping("/caisses")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','GUICHETIER','TREASURY_VIEW')")
    public ResponseEntity<List<Map<String, Object>>> listerCaisses() {
        return ResponseEntity.ok(tresorerieService.listerCaisses().stream().map(this::toCaisseMap).toList());
    }

    @PostMapping("/coffres")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','TREASURY_MANAGE')")
    @AuditLog(action = "COFFRE_CREATE", resource = "COFFRE")
    public ResponseEntity<Map<String, Object>> creerCoffre(@RequestBody Map<String, Object> payload) {
        ActionEnAttente action = pendingActionSubmissionService.submit("CREATE_COFFRE", "COFFRE", (String) payload.get("codeCoffre"), payload, "Creation coffre soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionMap(action));
    }

    @GetMapping("/coffres")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','GUICHETIER','TREASURY_VIEW')")
    public ResponseEntity<List<Map<String, Object>>> listerCoffres() {
        return ResponseEntity.ok(tresorerieService.listerCoffres().stream().map(this::toCoffreMap).toList());
    }

    @GetMapping("/coffres/{idCoffre}/mouvements")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','GUICHETIER','TREASURY_VIEW')")
    public ResponseEntity<List<Map<String, Object>>> listerMouvementsCoffre(@PathVariable Long idCoffre) {
        return ResponseEntity.ok(tresorerieService.listerMouvementsCoffre(idCoffre).stream().map(this::toMouvementCoffreMap).toList());
    }

    @PostMapping("/sessions")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','TREASURY_MANAGE')")
    @AuditLog(action = "CAISSE_OPEN", resource = "SESSION_CAISSE")
    public ResponseEntity<Map<String, Object>> ouvrirSession(@RequestBody Map<String, Object> payload) {
        ActionEnAttente action = pendingActionSubmissionService.submit("OPEN_SESSION_CAISSE", "SESSION_CAISSE", null, payload, "Ouverture de session soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionMap(action));
    }

    @PutMapping("/sessions/{idSession}/fermeture")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','TREASURY_MANAGE')")
    @AuditLog(action = "CAISSE_CLOSE", resource = "SESSION_CAISSE")
    public ResponseEntity<Map<String, Object>> fermerSession(@PathVariable Long idSession, @RequestBody Map<String, Object> payload) {
        ActionEnAttente action = pendingActionSubmissionService.submit("CLOSE_SESSION_CAISSE", "SESSION_CAISSE", String.valueOf(idSession), payload, "Fermeture de session soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionMap(action));
    }

    @GetMapping("/sessions")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','GUICHETIER','TREASURY_VIEW')")
    public ResponseEntity<List<Map<String, Object>>> listerSessions() {
        return ResponseEntity.ok(tresorerieService.listerSessions().stream().map(this::toSessionMap).toList());
    }

    @PostMapping("/approvisionnements")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','TREASURY_MANAGE')")
    @AuditLog(action = "CAISSE_APPROVISION", resource = "APPROVISIONNEMENT_CAISSE")
    public ResponseEntity<Map<String, Object>> approvisionnerCaisse(@RequestBody Map<String, Object> payload) {
        ActionEnAttente action = pendingActionSubmissionService.submit("APPROVISIONNEMENT_CAISSE", "APPROVISIONNEMENT_CAISSE", payload.get("referenceOperation") == null ? null : payload.get("referenceOperation").toString(), payload, "Approvisionnement caisse soumis");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionMap(action));
    }

    @PostMapping("/delestages")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','TREASURY_MANAGE')")
    @AuditLog(action = "CAISSE_DELESTAGE", resource = "DELESTAGE_CAISSE")
    public ResponseEntity<Map<String, Object>> delesterCaisse(@RequestBody Map<String, Object> payload) {
        ActionEnAttente action = pendingActionSubmissionService.submit("DELESTAGE_CAISSE", "DELESTAGE_CAISSE", payload.get("referenceOperation") == null ? null : payload.get("referenceOperation").toString(), payload, "Delestage caisse soumis");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionMap(action));
    }

    private Map<String, Object> toCaisseMap(Caisse caisse) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("idCaisse", caisse.getIdCaisse());
        response.put("codeCaisse", caisse.getCodeCaisse());
        response.put("libelle", caisse.getLibelle());
        response.put("agence", caisse.getAgence().getNomAgence());
        response.put("guichet", caisse.getGuichet() == null ? null : caisse.getGuichet().getNomGuichet());
        response.put("statut", caisse.getStatut());
        response.put("soldeTheorique", caisse.getSoldeTheorique());
        return response;
    }

    private Map<String, Object> toCoffreMap(Coffre coffre) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("idCoffre", coffre.getIdCoffre());
        response.put("codeCoffre", coffre.getCodeCoffre());
        response.put("libelle", coffre.getLibelle());
        response.put("agence", coffre.getAgence().getNomAgence());
        response.put("soldeTheorique", coffre.getSoldeTheorique());
        response.put("statut", coffre.getStatut());
        return response;
    }

    private Map<String, Object> toSessionMap(SessionCaisse session) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("idSessionCaisse", session.getIdSessionCaisse());
        response.put("idCaisse", session.getCaisse().getIdCaisse());
        response.put("idUtilisateur", session.getUtilisateur().getIdUser());
        response.put("dateOuverture", session.getDateOuverture());
        response.put("dateFermeture", session.getDateFermeture());
        response.put("soldeOuverture", session.getSoldeOuverture());
        response.put("soldeTheoriqueFermeture", session.getSoldeTheoriqueFermeture());
        response.put("soldePhysiqueFermeture", session.getSoldePhysiqueFermeture());
        response.put("ecart", session.getEcart());
        response.put("statut", session.getStatut());
        return response;
    }

    private Map<String, Object> toMouvementCoffreMap(MouvementCoffre mouvementCoffre) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("idMouvementCoffre", mouvementCoffre.getIdMouvementCoffre());
        response.put("typeMouvement", mouvementCoffre.getTypeMouvement());
        response.put("montant", mouvementCoffre.getMontant());
        response.put("referenceMouvement", mouvementCoffre.getReferenceMouvement());
        response.put("commentaire", mouvementCoffre.getCommentaire());
        return response;
    }

    private Map<String, Object> toApprovisionnementMap(ApprovisionnementCaisse approvisionnement) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("idApprovisionnementCaisse", approvisionnement.getIdApprovisionnementCaisse());
        response.put("idCoffre", approvisionnement.getCoffre().getIdCoffre());
        response.put("idCaisse", approvisionnement.getCaisse().getIdCaisse());
        response.put("montant", approvisionnement.getMontant());
        response.put("referenceOperation", approvisionnement.getReferenceOperation());
        return response;
    }

    private Map<String, Object> toDelestageMap(DelestageCaisse delestage) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("idDelestageCaisse", delestage.getIdDelestageCaisse());
        response.put("idCoffre", delestage.getCoffre().getIdCoffre());
        response.put("idCaisse", delestage.getCaisse().getIdCaisse());
        response.put("montant", delestage.getMontant());
        response.put("referenceOperation", delestage.getReferenceOperation());
        return response;
    }

    private Map<String, Object> toActionMap(ActionEnAttente action) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("idActionEnAttente", action.getIdActionEnAttente());
        response.put("typeAction", action.getTypeAction());
        response.put("ressource", action.getRessource());
        response.put("statut", action.getStatut());
        return response;
    }
}
