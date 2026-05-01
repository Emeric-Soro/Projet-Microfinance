package com.microfinance.core_banking.service.extension;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microfinance.core_banking.entity.ActionEnAttente;
import org.springframework.stereotype.Service;
// imports above are consolidated
import com.microfinance.core_banking.entity.LoanFacility;
import com.microfinance.core_banking.repository.extension.LoanFacilityRepository;

import java.util.Map;

@Service
public class PendingActionExecutionService {

    private final ObjectMapper objectMapper;
    private final LoanFacilityRepository loanFacilityRepository;
    private final OrganisationService organisationService;
    private final CreditExtensionService creditExtensionService;
    private final EpargneExtensionService epargneExtensionService;
    private final TresorerieService tresorerieService;
    private final DigitalExtensionService digitalExtensionService;
    private final ComptabiliteExtensionService comptabiliteExtensionService;
    private final PermissionSecuriteService permissionSecuriteService;
    private final RoleUtilisateurService roleUtilisateurService;
    private final ConformiteExtensionService conformiteExtensionService;
    private final PaiementExterneService paiementExterneService;
    private final RisqueExtensionService risqueExtensionService;
    private final SupportEntrepriseService supportEntrepriseService;

    public PendingActionExecutionService(
            ObjectMapper objectMapper,
            OrganisationService organisationService,
            CreditExtensionService creditExtensionService,
            EpargneExtensionService epargneExtensionService,
            TresorerieService tresorerieService,
            DigitalExtensionService digitalExtensionService,
            ComptabiliteExtensionService comptabiliteExtensionService,
            PermissionSecuriteService permissionSecuriteService,
            RoleUtilisateurService roleUtilisateurService,
            ConformiteExtensionService conformiteExtensionService,
            PaiementExterneService paiementExterneService,
            RisqueExtensionService risqueExtensionService,
            SupportEntrepriseService supportEntrepriseService,
            LoanFacilityRepository loanFacilityRepository
    ) {
        this.objectMapper = objectMapper;
        this.organisationService = organisationService;
        this.creditExtensionService = creditExtensionService;
        this.epargneExtensionService = epargneExtensionService;
        this.tresorerieService = tresorerieService;
        this.digitalExtensionService = digitalExtensionService;
        this.comptabiliteExtensionService = comptabiliteExtensionService;
        this.permissionSecuriteService = permissionSecuriteService;
        this.roleUtilisateurService = roleUtilisateurService;
        this.conformiteExtensionService = conformiteExtensionService;
        this.paiementExterneService = paiementExterneService;
        this.risqueExtensionService = risqueExtensionService;
        this.supportEntrepriseService = supportEntrepriseService;
        this.loanFacilityRepository = loanFacilityRepository;
    }

    public String execute(ActionEnAttente action) {
        Map<String, Object> payload = readPayload(action.getNouvelleValeur());
        return switch (action.getTypeAction()) {
            case "CREATE_AGENCE" -> String.valueOf(organisationService.creerAgence(payload).getIdAgence());
            case "CREATE_GUICHET" -> String.valueOf(organisationService.creerGuichet(payload).getIdGuichet());
            case "ASSIGN_USER_AGENCE" -> String.valueOf(organisationService.affecterUtilisateur(payload).getIdAffectation());
            case "CREATE_PARAMETRE_AGENCE" -> String.valueOf(organisationService.creerParametreAgence(payload).getIdParametreAgence());
            case "CREATE_MUTATION_PERSONNEL" -> String.valueOf(organisationService.creerMutationPersonnel(payload).getIdMutationPersonnel());
            case "CREATE_COMPTE_LIAISON_AGENCE" -> String.valueOf(organisationService.creerCompteLiaison(payload).getIdCompteLiaisonAgence());
            case "CREATE_PRODUIT_CREDIT" -> String.valueOf(creditExtensionService.creerProduit(payload).getIdProduitCredit());
            case "DECISION_DEMANDE_CREDIT" -> String.valueOf(creditExtensionService.deciderDemande(
                    Long.valueOf(action.getReferenceRessource()),
                    payload
            ).getIdDemandeCredit());
            case "DEBLOCAGE_CREDIT" -> String.valueOf(creditExtensionService.debloquerCredit(Long.valueOf(payload.get("idDemande").toString()), payload).getIdCredit());
            case "CREATE_GARANTIE_CREDIT" -> String.valueOf(creditExtensionService.enregistrerGarantie(
                    Long.valueOf(action.getReferenceRessource()),
                    payload
            ).getIdGarantieCredit());
            case "CALCUL_PROVISION_CREDIT" -> String.valueOf(creditExtensionService.calculerProvisions(payload).size());
            case "DETECTION_IMPAYE_CREDIT" -> String.valueOf(creditExtensionService.detecterImpayes(payload).size());
            case "CREATE_PRODUIT_EPARGNE" -> String.valueOf(epargneExtensionService.creerProduit(payload).getIdProduitEpargne());
            case "CREATE_CAISSE" -> String.valueOf(tresorerieService.creerCaisse(payload).getIdCaisse());
            case "CREATE_COFFRE" -> String.valueOf(tresorerieService.creerCoffre(payload).getIdCoffre());
            case "OPEN_SESSION_CAISSE" -> String.valueOf(tresorerieService.ouvrirSession(payload).getIdSessionCaisse());
            case "CLOSE_SESSION_CAISSE" -> String.valueOf(tresorerieService.fermerSession(Long.valueOf(action.getReferenceRessource()), payload).getIdSessionCaisse());
            case "APPROVISIONNEMENT_CAISSE" -> String.valueOf(tresorerieService.approvisionnerCaisse(payload).getIdApprovisionnementCaisse());
            case "DELESTAGE_CAISSE" -> String.valueOf(tresorerieService.delesterCaisse(payload).getIdDelestageCaisse());
            case "CREATE_PARTENAIRE_API" -> String.valueOf(digitalExtensionService.creerPartenaire(payload).getIdPartenaireApi());
            case "CREATE_EMPLOYE" -> String.valueOf(digitalExtensionService.creerEmploye(payload).getIdEmploye());
            case "CREATE_OPERATEUR_MOBILE_MONEY" -> String.valueOf(paiementExterneService.creerOperateur(payload).getIdOperateurMobileMoney());
            case "CREATE_WALLET_CLIENT" -> String.valueOf(paiementExterneService.creerWallet(payload).getIdWalletClient());
            case "CREATE_TRANSACTION_MOBILE_MONEY" -> String.valueOf(paiementExterneService.enregistrerTransactionMobileMoney(payload).getIdTransactionMobileMoney());
            case "UPDATE_STATUT_TRANSACTION_MOBILE_MONEY" -> String.valueOf(paiementExterneService.changerStatutTransactionMobileMoney(
                    Long.valueOf(action.getReferenceRessource()),
                    payload
            ).getIdTransactionMobileMoney());
            case "CREATE_LOT_COMPENSATION" -> String.valueOf(paiementExterneService.creerLotCompensation(payload).getIdLotCompensation());
            case "CREATE_ORDRE_PAIEMENT_EXTERNE" -> String.valueOf(paiementExterneService.initierOrdrePaiement(payload).getIdOrdrePaiementExterne());
            case "UPDATE_STATUT_ORDRE_PAIEMENT_EXTERNE" -> String.valueOf(paiementExterneService.changerStatutOrdre(
                    Long.valueOf(action.getReferenceRessource()),
                    payload
            ).getIdOrdrePaiementExterne());
            case "CREATE_CLASSE_COMPTABLE" -> String.valueOf(comptabiliteExtensionService.creerClasse(payload).getIdClasseComptable());
            case "CREATE_COMPTE_COMPTABLE" -> String.valueOf(comptabiliteExtensionService.creerCompte(payload).getIdCompteComptable());
            case "CREATE_JOURNAL_COMPTABLE" -> String.valueOf(comptabiliteExtensionService.creerJournal(payload).getIdJournalComptable());
            case "CREATE_SCHEMA_COMPTABLE" -> String.valueOf(comptabiliteExtensionService.creerSchema(payload).getIdSchemaComptable());
            case "CREATE_ECRITURE_MANUELLE" -> String.valueOf(comptabiliteExtensionService.creerEcritureManuelle(payload).getIdEcritureComptable());
            case "CLOTURE_COMPTABLE" -> String.valueOf(comptabiliteExtensionService.cloturerPeriode(payload).getIdClotureComptable());
            case "CREATE_PERMISSION" -> String.valueOf(permissionSecuriteService.appliquerCreation(payload).getIdPermission());
            case "UPDATE_PERMISSION" -> String.valueOf(permissionSecuriteService.appliquerMiseAJour(Long.valueOf(action.getReferenceRessource()), payload).getIdPermission());
            case "DELETE_PERMISSION" -> {
                permissionSecuriteService.appliquerSuppression(Long.valueOf(action.getReferenceRessource()));
                yield action.getReferenceRessource();
            }
            case "ASSIGN_PERMISSION_ROLE" -> String.valueOf(permissionSecuriteService.appliquerAffectationRole(
                    Long.valueOf(payload.get("idRole").toString()),
                    Long.valueOf(payload.get("idPermission").toString())
            ).getIdRole());
            case "REVOKE_PERMISSION_ROLE" -> String.valueOf(permissionSecuriteService.appliquerRevocationRole(
                    Long.valueOf(payload.get("idRole").toString()),
                    Long.valueOf(payload.get("idPermission").toString())
            ).getIdRole());
            case "CREATE_ROLE" -> String.valueOf(roleUtilisateurService.applyCreateRole(
                    payload.get("codeRoleUtilisateur").toString(),
                    payload.get("intituleRole").toString()
            ).getIdRole());
            case "UPDATE_ROLE" -> String.valueOf(roleUtilisateurService.applyUpdateRole(
                    Long.valueOf(action.getReferenceRessource()),
                    payload.get("codeRoleUtilisateur").toString(),
                    payload.get("intituleRole").toString()
            ).getIdRole());
            case "DELETE_ROLE" -> {
                roleUtilisateurService.applyDeleteRole(Long.valueOf(action.getReferenceRessource()));
                yield action.getReferenceRessource();
            }
            case "CREATE_ALERTE_CONFORMITE" -> String.valueOf(conformiteExtensionService.creerAlerte(payload).getIdAlerteConformite());
            case "CREATE_RAPPORT_REGLEMENTAIRE" -> String.valueOf(conformiteExtensionService.creerRapport(payload).getIdRapportReglementaire());
            case "RESCAN_CLIENT_CONFORMITE" -> String.valueOf(conformiteExtensionService.rescannerClient(
                    Long.valueOf(action.getReferenceRessource()),
                    payload
            ).getIdAlerteConformite());
            case "RESCAN_TRANSACTION_CONFORMITE" -> String.valueOf(conformiteExtensionService.rescannerTransaction(
                    Long.valueOf(action.getReferenceRessource())
            ).getIdAlerteConformite());
            case "BIC_CONSULTATION" -> String.valueOf(conformiteExtensionService.enregistrerConsultationBic(payload).getIdRapportReglementaire());
            case "RAPPORT_PRUDENTIEL" -> String.valueOf(conformiteExtensionService.genererRapportPrudentiel(payload).getIdRapportReglementaire());
            case "RAPPORT_FISCAL" -> String.valueOf(conformiteExtensionService.genererRapportFiscal(payload).getIdRapportReglementaire());
            case "CREATE_RISQUE" -> String.valueOf(risqueExtensionService.creerRisque(payload).getIdRisque());
            case "DECLARE_INCIDENT_OPERATIONNEL" -> String.valueOf(risqueExtensionService.declarerIncident(payload).getIdIncidentOperationnel());
            case "CREATE_STRESS_TEST" -> String.valueOf(risqueExtensionService.creerStressTest(payload).getIdStressTest());
            case "EXECUTE_STRESS_TEST" -> String.valueOf(risqueExtensionService.executerStressTest(
                    Long.valueOf(action.getReferenceRessource())
            ).getIdResultatStressTest());
            case "CREATE_BUDGET" -> String.valueOf(supportEntrepriseService.creerBudget(payload).getIdBudget());
            case "CREATE_FOURNISSEUR" -> String.valueOf(supportEntrepriseService.creerFournisseur(payload).getIdFournisseur());
            case "CREATE_COMMANDE_ACHAT" -> String.valueOf(supportEntrepriseService.creerCommandeAchat(payload).getIdCommandeAchat());
            case "CREATE_BULLETIN_PAIE" -> String.valueOf(supportEntrepriseService.genererBulletinPaie(payload).getIdBulletinPaie());
            case "CREATE_IMMOBILISATION" -> String.valueOf(supportEntrepriseService.creerImmobilisation(payload).getIdImmobilisation());
            case "CREATE_LOAN_FACILITY" -> {
                // Expect payload to contain the full loan data to create or update a loan
                if (action.getReferenceRessource() != null) {
                    Long loanId = Long.valueOf(action.getReferenceRessource());
                    LoanFacility existing = loanFacilityRepository.findById(loanId)
                            .orElseThrow(() -> new IllegalArgumentException("LoanFacility introuvable: " + loanId));
                    LoanFacility updated = objectMapper.convertValue(payload, LoanFacility.class);
                    // Copy fields from updated into existing (simple approach)
                    existing.setCustomerId(updated.getCustomerId());
                    existing.setProductId(updated.getProductId());
                    existing.setPrincipalAmount(updated.getPrincipalAmount());
                    existing.setOutstandingBalance(updated.getOutstandingBalance());
                    existing.setInterestRate(updated.getInterestRate());
                    existing.setTermMonths(updated.getTermMonths());
                    existing.setStartDate(updated.getStartDate());
                    existing.setEndDate(updated.getEndDate());
                    if (updated.getStatus() != null) {
                        existing.setStatus(updated.getStatus());
                    }
                    LoanFacility saved = loanFacilityRepository.save(existing);
                    yield String.valueOf(saved.getId());
                } else {
                    LoanFacility toCreate = objectMapper.convertValue(payload, LoanFacility.class);
                    LoanFacility saved = loanFacilityRepository.save(toCreate);
                    yield String.valueOf(saved.getId());
                }
            }
            default -> throw new IllegalArgumentException("Type d'action non supporte: " + action.getTypeAction());
        };
    }

    private Map<String, Object> readPayload(String rawPayload) {
        try {
            return objectMapper.readValue(rawPayload, new TypeReference<>() { });
        } catch (Exception exception) {
            throw new IllegalArgumentException("Impossible de deserialiser l'action en attente", exception);
        }
    }
}
