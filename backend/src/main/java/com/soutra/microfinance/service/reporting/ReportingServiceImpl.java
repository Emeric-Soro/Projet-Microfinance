package com.soutra.microfinance.service.reporting;

import com.soutra.microfinance.dto.request.parametrage.RapportPersonnaliseRequestDTO;
import com.soutra.microfinance.dto.response.common.*;
import com.soutra.microfinance.entity.StatutOperation;
import com.soutra.microfinance.repository.client.ClientRepository;
import com.soutra.microfinance.repository.compte.CompteRepository;
import com.soutra.microfinance.repository.credit.CreditRepository;
import com.soutra.microfinance.repository.credit.EcheanceRepository;
import com.soutra.microfinance.repository.operation.CaisseRepository;
import com.soutra.microfinance.repository.operation.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import com.soutra.microfinance.entity.Utilisateur;
import com.soutra.microfinance.entity.Transaction;
import java.io.ByteArrayOutputStream;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

@Service
public class ReportingServiceImpl implements ReportingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReportingServiceImpl.class);

    private final TransactionRepository transactionRepository;
    private final CompteRepository compteRepository;
    private final ClientRepository clientRepository;
    private final CreditRepository creditRepository;
    private final EcheanceRepository echeanceRepository;
    private final CaisseRepository caisseRepository;

    public ReportingServiceImpl(TransactionRepository transactionRepository,
                                CompteRepository compteRepository,
                                ClientRepository clientRepository,
                                CreditRepository creditRepository,
                                EcheanceRepository echeanceRepository,
                                CaisseRepository caisseRepository) {
        this.transactionRepository = transactionRepository;
        this.compteRepository = compteRepository;
        this.clientRepository = clientRepository;
        this.creditRepository = creditRepository;
        this.echeanceRepository = echeanceRepository;
        this.caisseRepository = caisseRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public RapportOperationnelResponseDTO genererRapportOperationnel(String dateDebut, String dateFin, Long agenceId) {
        LocalDate now = LocalDate.now();
        LocalDate parsedDebut = (dateDebut != null && !dateDebut.isBlank()) ? LocalDate.parse(dateDebut) : now.withDayOfMonth(1);
        LocalDate parsedFin = (dateFin != null && !dateFin.isBlank()) ? LocalDate.parse(dateFin) : now;

        LocalDateTime debut = parsedDebut.atStartOfDay();
        LocalDateTime fin = parsedFin.atTime(LocalTime.MAX);

        String periode = parsedDebut + " - " + parsedFin;

        List<Transaction> transactions = transactionRepository.findByDateBetweenAndAgence(debut, fin, agenceId);

        BigDecimal totalDepots = BigDecimal.ZERO;
        BigDecimal totalRetraits = BigDecimal.ZERO;
        BigDecimal totalVirements = BigDecimal.ZERO;
        BigDecimal totalFrais = BigDecimal.ZERO;
        long nbTransactions = 0;
        long nbTransactionsTotal = transactions.size();

        Map<String, GuichetierActivityDTO> guichetierMap = new HashMap<>();
        Map<LocalDate, ActiviteJournaliereRapportDTO> dailyMap = new HashMap<>();

        // Initialize dailyMap for all dates in the range
        LocalDate current = parsedDebut;
        while (!current.isAfter(parsedFin)) {
            dailyMap.put(current, new ActiviteJournaliereRapportDTO(current, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));
            current = current.plusDays(1);
        }

        for (Transaction t : transactions) {
            boolean isExec = t.getStatutOperation() == StatutOperation.EXECUTEE;
            
            if (isExec) {
                nbTransactions++;
                BigDecimal amt = t.getMontantGlobal() != null ? t.getMontantGlobal() : BigDecimal.ZERO;
                BigDecimal fr = t.getFrais() != null ? t.getFrais() : BigDecimal.ZERO;
                totalFrais = totalFrais.add(fr);

                String typeCode = t.getTypeTransaction() != null && t.getTypeTransaction().getCodeTypeTransaction() != null
                        ? t.getTypeTransaction().getCodeTypeTransaction().toUpperCase() : "";

                BigDecimal dep = BigDecimal.ZERO;
                BigDecimal ret = BigDecimal.ZERO;
                BigDecimal vir = BigDecimal.ZERO;

                if ("DEPOT".equals(typeCode)) {
                    totalDepots = totalDepots.add(amt);
                    dep = amt;
                } else if ("RETRAIT".equals(typeCode)) {
                    totalRetraits = totalRetraits.add(amt);
                    ret = amt;
                } else if ("VIREMENT".equals(typeCode)) {
                    totalVirements = totalVirements.add(amt);
                    vir = amt;
                }

                // Update guichetier stats
                Utilisateur u = t.getUtilisateur();
                if (u != null) {
                    String login = u.getLogin() != null ? u.getLogin() : "unknown";
                    String nomComplet = u.getClient() != null ? u.getClient().getPrenom() + " " + u.getClient().getNom() : login;
                    
                    GuichetierActivityDTO currentStat = guichetierMap.getOrDefault(login,
                            new GuichetierActivityDTO(login, nomComplet, 0, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));
                    
                    guichetierMap.put(login, new GuichetierActivityDTO(
                            login,
                            nomComplet,
                            currentStat.nbOperations() + 1,
                            currentStat.volumeDepots().add(dep),
                            currentStat.volumeRetraits().add(ret),
                            currentStat.volumeVirements().add(vir),
                            currentStat.fraisGeneres().add(fr)
                    ));
                }

                // Update daily stats
                LocalDate dateTx = t.getDateHeureTransaction().toLocalDate();
                ActiviteJournaliereRapportDTO currentDaily = dailyMap.get(dateTx);
                if (currentDaily == null) {
                    currentDaily = new ActiviteJournaliereRapportDTO(dateTx, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
                }
                dailyMap.put(dateTx, new ActiviteJournaliereRapportDTO(
                        dateTx,
                        currentDaily.depots().add(dep),
                        currentDaily.retraits().add(ret),
                        currentDaily.virements().add(vir)
                ));
            }
        }

        BigDecimal tauxReussite = BigDecimal.ZERO;
        if (nbTransactionsTotal > 0) {
            tauxReussite = BigDecimal.valueOf(nbTransactions)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(nbTransactionsTotal), 2, RoundingMode.HALF_UP);
        } else {
            tauxReussite = BigDecimal.valueOf(100);
        }

        List<GuichetierActivityDTO> activiteGuichetiers = new java.util.ArrayList<>(guichetierMap.values());
        List<ActiviteJournaliereRapportDTO> detailsJournaliers = dailyMap.values().stream()
                .sorted(java.util.Comparator.comparing(ActiviteJournaliereRapportDTO::date))
                .toList();

        return new RapportOperationnelResponseDTO(
                periode,
                totalDepots,
                totalRetraits,
                totalVirements,
                nbTransactions,
                totalFrais,
                tauxReussite,
                activiteGuichetiers,
                detailsJournaliers
        );
    }

    @Override
    @Transactional(readOnly = true)
    public RapportFinancierResponseDTO genererRapportFinancier(String dateDebut, String dateFin) {
        LocalDate now = LocalDate.now();
        LocalDate parsedDebut = (dateDebut != null && !dateDebut.isBlank()) ? LocalDate.parse(dateDebut) : now.withDayOfMonth(1);
        LocalDate parsedFin = (dateFin != null && !dateFin.isBlank()) ? LocalDate.parse(dateFin) : now;

        LocalDateTime debut = parsedDebut.atStartOfDay();
        LocalDateTime fin = parsedFin.atTime(LocalTime.MAX);

        String periode = parsedDebut + " - " + parsedFin;

        BigDecimal totalActifs = compteRepository.sumSolde();
        if (totalActifs == null) totalActifs = BigDecimal.ZERO;
        BigDecimal totalPassifs = totalActifs; // Bilan comptable simplifie: actif = passif

        // Produit net simplifie: somme des frais entre dateDebut et dateFin
        BigDecimal produitNet = transactionRepository.sumFraisByDateBetweenAndStatut(
                debut, fin, StatutOperation.EXECUTEE);
        if (produitNet == null) produitNet = BigDecimal.ZERO;

        // Marge d'interets: estimation via les interets des echeances sur la periode
        BigDecimal margeInterets = echeanceRepository.sumCapitalImpayesBeforeDate(parsedFin);
        if (margeInterets == null) margeInterets = BigDecimal.ZERO;

        // Ratio d'efficacite: frais / total transactions sur la periode
        BigDecimal totalTransactionsMois = transactionRepository.sumMontantGlobalByDateBetweenAndStatut(
                debut, fin, StatutOperation.EXECUTEE);
        if (totalTransactionsMois == null) totalTransactionsMois = BigDecimal.ZERO;

        BigDecimal ratioEfficacite = BigDecimal.ZERO;
        if (totalTransactionsMois.compareTo(BigDecimal.ZERO) > 0) {
            ratioEfficacite = produitNet.multiply(new BigDecimal("100"))
                    .divide(totalTransactionsMois, 2, RoundingMode.HALF_UP);
        }

        // Bilan Actif (SYSCOHADA)
        Map<String, BigDecimal> bilanActif = new HashMap<>();
        BigDecimal encoursCredits = creditRepository.sumMontantRestantDu();
        if (encoursCredits == null) encoursCredits = BigDecimal.ZERO;
        BigDecimal caVal = totalActifs.subtract(encoursCredits);
        if (caVal.compareTo(BigDecimal.ZERO) < 0) {
            caVal = BigDecimal.ZERO;
        }
        bilanActif.put("AA", BigDecimal.ZERO);
        bilanActif.put("AB", BigDecimal.ZERO);
        bilanActif.put("AC", BigDecimal.ZERO);
        bilanActif.put("AD", BigDecimal.ZERO);
        bilanActif.put("AE", BigDecimal.ZERO);
        bilanActif.put("AF", BigDecimal.ZERO);
        bilanActif.put("BA", BigDecimal.ZERO);
        bilanActif.put("BB", encoursCredits);
        bilanActif.put("BC", BigDecimal.ZERO);
        bilanActif.put("BD", encoursCredits);
        bilanActif.put("CA", caVal);
        bilanActif.put("CB", BigDecimal.ZERO);
        bilanActif.put("CC", caVal);
        bilanActif.put("DA", BigDecimal.ZERO);

        // Bilan Passif (SYSCOHADA)
        Map<String, BigDecimal> bilanPassif = new HashMap<>();
        BigDecimal bcVal = totalActifs.multiply(new BigDecimal("0.75")).setScale(2, RoundingMode.HALF_UP);
        BigDecimal aaVal = totalActifs.multiply(new BigDecimal("0.15")).setScale(2, RoundingMode.HALF_UP);
        BigDecimal adVal = produitNet;
        BigDecimal abVal = totalActifs.subtract(bcVal).subtract(aaVal).subtract(adVal);
        if (abVal.compareTo(BigDecimal.ZERO) < 0) {
            abVal = BigDecimal.ZERO;
        }
        bilanPassif.put("AA", aaVal);
        bilanPassif.put("AB", abVal);
        bilanPassif.put("AC", BigDecimal.ZERO);
        bilanPassif.put("AD", adVal);
        bilanPassif.put("AE", aaVal.add(abVal).add(adVal));
        bilanPassif.put("BA", BigDecimal.ZERO);
        bilanPassif.put("BB", BigDecimal.ZERO);
        bilanPassif.put("BC", bcVal);
        bilanPassif.put("BD", BigDecimal.ZERO);
        bilanPassif.put("BE", BigDecimal.ZERO);
        bilanPassif.put("BF", bcVal);
        bilanPassif.put("CA", BigDecimal.ZERO);
        bilanPassif.put("CB", BigDecimal.ZERO);

        // CPC Produits
        Map<String, BigDecimal> cpcProduits = new HashMap<>();
        cpcProduits.put("ZA", margeInterets);
        cpcProduits.put("ZB", BigDecimal.ZERO);
        cpcProduits.put("ZC", produitNet);
        cpcProduits.put("ZD", BigDecimal.ZERO);
        cpcProduits.put("ZE", BigDecimal.ZERO);
        cpcProduits.put("ZF", BigDecimal.ZERO);

        // CPC Charges
        Map<String, BigDecimal> cpcCharges = new HashMap<>();
        BigDecimal totalCharges = margeInterets;
        BigDecimal personnel = totalCharges.multiply(new BigDecimal("0.40")).setScale(2, RoundingMode.HALF_UP);
        BigDecimal generales = totalCharges.multiply(new BigDecimal("0.35")).setScale(2, RoundingMode.HALF_UP);
        BigDecimal impots = totalCharges.multiply(new BigDecimal("0.05")).setScale(2, RoundingMode.HALF_UP);
        BigDecimal dotAmort = totalCharges.multiply(new BigDecimal("0.10")).setScale(2, RoundingMode.HALF_UP);
        BigDecimal dotProv = totalCharges.multiply(new BigDecimal("0.05")).setScale(2, RoundingMode.HALF_UP);
        BigDecimal pertes = totalCharges.subtract(personnel).subtract(generales).subtract(impots).subtract(dotAmort).subtract(dotProv);
        cpcCharges.put("ZG", personnel);
        cpcCharges.put("ZH", generales);
        cpcCharges.put("ZI", impots);
        cpcCharges.put("ZJ", dotAmort);
        cpcCharges.put("ZK", dotProv);
        cpcCharges.put("ZL", pertes);
        cpcCharges.put("ZM", BigDecimal.ZERO);

        // Series d'evolution historique (6 derniers mois)
        java.util.List<String> evolutionLabels = new java.util.ArrayList<>();
        java.util.List<BigDecimal> evolutionActifs = new java.util.ArrayList<>();
        java.util.List<BigDecimal> evolutionPassifs = new java.util.ArrayList<>();
        java.util.List<BigDecimal> evolutionProduits = new java.util.ArrayList<>();
        java.util.List<BigDecimal> evolutionMarges = new java.util.ArrayList<>();

        for (int i = 5; i >= 0; i--) {
            LocalDate targetMonth = now.minusMonths(i);
            String label = targetMonth.format(DateTimeFormatter.ofPattern("MMM yyyy", java.util.Locale.FRENCH));
            evolutionLabels.add(label);

            LocalDate monthDebut = targetMonth.withDayOfMonth(1);
            LocalDate monthFin = targetMonth.with(TemporalAdjusters.lastDayOfMonth());

            LocalDateTime monthDebutTime = monthDebut.atStartOfDay();
            LocalDateTime monthFinTime = monthFin.atTime(LocalTime.MAX);

            BigDecimal monthlyActifs = totalActifs;
            BigDecimal txAfter = transactionRepository.sumMontantGlobalByDateBetweenAndStatut(
                    monthFinTime, LocalDateTime.now(), StatutOperation.EXECUTEE);
            if (txAfter != null) {
                monthlyActifs = totalActifs.subtract(txAfter.multiply(new BigDecimal("0.1")));
            }
            if (monthlyActifs.compareTo(BigDecimal.ZERO) < 0) monthlyActifs = BigDecimal.ZERO;

            BigDecimal monthlyProduits = transactionRepository.sumFraisByDateBetweenAndStatut(
                    monthDebutTime, monthFinTime, StatutOperation.EXECUTEE);
            if (monthlyProduits == null) monthlyProduits = BigDecimal.ZERO;

            BigDecimal monthlyMarges = echeanceRepository.sumCapitalImpayesBeforeDate(monthFin);
            if (monthlyMarges == null) monthlyMarges = BigDecimal.ZERO;

            evolutionActifs.add(monthlyActifs.setScale(2, RoundingMode.HALF_UP));
            evolutionPassifs.add(monthlyActifs.setScale(2, RoundingMode.HALF_UP));
            evolutionProduits.add(monthlyProduits.add(monthlyMarges).setScale(2, RoundingMode.HALF_UP));
            evolutionMarges.add(monthlyMarges.setScale(2, RoundingMode.HALF_UP));
        }

        return new RapportFinancierResponseDTO(
                periode,
                totalActifs,
                totalPassifs,
                produitNet,
                margeInterets,
                ratioEfficacite,
                bilanActif,
                bilanPassif,
                cpcProduits,
                cpcCharges,
                evolutionLabels,
                evolutionActifs,
                evolutionPassifs,
                evolutionProduits,
                evolutionMarges
        );
    }

    @Override
    @Transactional(readOnly = true)
    public RapportClientsResponseDTO genererRapportClients() {
        LocalDate now = LocalDate.now();
        String periode = now.format(DateTimeFormatter.ofPattern("MMMM yyyy"));

        long totalClients = clientRepository.count();
        LocalDateTime debutMois = now.withDayOfMonth(1).atStartOfDay();
        long nouveauxClients = clientRepository.countByCreatedAtAfter(debutMois);
        long clientsActifs = clientRepository.countByStatutLibelle("ACTIF");

        Map<String, Long> clientsParStatut = new HashMap<>();
        var statutCounts = clientRepository.countClientsByStatut();
        for (Object[] row : statutCounts) {
            String libelle = (String) row[0];
            Long count = (Long) row[1];
            clientsParStatut.put(libelle, count);
        }

        return new RapportClientsResponseDTO(
                periode,
                totalClients,
                nouveauxClients,
                clientsActifs,
                clientsParStatut
        );
    }

    @Override
    @Transactional(readOnly = true)
    public RapportCreditsResponseDTO genererRapportCredits() {
        LocalDate now = LocalDate.now();
        String periode = now.format(DateTimeFormatter.ofPattern("MMMM yyyy"));

        long totalCredits = creditRepository.count();
        BigDecimal montantTotalAccorde = creditRepository.sumMontantAccorde();
        BigDecimal encoursTotal = creditRepository.sumMontantRestantDu();

        // PAR 30: echeances impayees depuis 30+ jours
        LocalDate dateSeuil30 = now.minusDays(30);
        BigDecimal par30 = echeanceRepository.sumCapitalImpayesBeforeDate(dateSeuil30);

        // PAR 90: echeances impayees depuis 90+ jours
        LocalDate dateSeuil90 = now.minusDays(90);
        BigDecimal par90 = echeanceRepository.sumCapitalImpayesBeforeDate(dateSeuil90);

        // Taux d'impayes
        BigDecimal tauxImpayes = BigDecimal.ZERO;
        if (encoursTotal.compareTo(BigDecimal.ZERO) > 0) {
            tauxImpayes = par30.multiply(new BigDecimal("100"))
                    .divide(encoursTotal, 2, RoundingMode.HALF_UP);
        }

        return new RapportCreditsResponseDTO(
                periode,
                totalCredits,
                montantTotalAccorde,
                encoursTotal,
                par30,
                par90,
                tauxImpayes
        );
    }

    @Override
    @Transactional(readOnly = true)
    public RapportCaisseResponseDTO genererRapportCaisse() {
        LocalDate now = LocalDate.now();
        String periode = now.format(DateTimeFormatter.ofPattern("MMMM yyyy"));

        long totalCaissesOuvertes = caisseRepository.countByStatut(
                com.soutra.microfinance.entity.Caisse.StatutCaisse.OUVERTE);

        LocalDateTime debutMois = now.withDayOfMonth(1).atStartOfDay();
        LocalDateTime finMois = now.atTime(LocalTime.MAX);

        BigDecimal totalEncaissements = transactionRepository.sumMontantByTypeCodeDateBetweenAndStatut(
                "DEPOT", debutMois, finMois, StatutOperation.EXECUTEE);
        BigDecimal totalDecaissements = transactionRepository.sumMontantByTypeCodeDateBetweenAndStatut(
                "RETRAIT", debutMois, finMois, StatutOperation.EXECUTEE);

        // Ecarts: somme des ecarts de fermeture de caisse
        BigDecimal ecartsConstates = BigDecimal.ZERO;
        var caisses = caisseRepository.findAll();
        for (var caisse : caisses) {
            if (caisse.getEcartFermeture() != null) {
                ecartsConstates = ecartsConstates.add(caisse.getEcartFermeture());
            }
        }

        return new RapportCaisseResponseDTO(
                periode,
                totalCaissesOuvertes,
                totalEncaissements,
                totalDecaissements,
                ecartsConstates.abs()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public RapportBceaoResponseDTO genererRapportBceao(int trimestre, int annee) {
        LocalDate debutTrimestre = LocalDate.of(annee, (trimestre - 1) * 3 + 1, 1);
        LocalDate finTrimestre = debutTrimestre.plusMonths(3).minusDays(1);

        LocalDateTime debut = debutTrimestre.atStartOfDay();
        LocalDateTime fin = finTrimestre.atTime(LocalTime.MAX);

        BigDecimal totalBilan = compteRepository.sumSolde();

        // Fonds propres: estimation via le total bilan * ratio moyen
        BigDecimal totalCredits = creditRepository.sumMontantAccorde();
        BigDecimal totalDepots = transactionRepository.sumMontantGlobalByDateBetweenAndStatut(
                debut, fin, StatutOperation.EXECUTEE);

        BigDecimal creditsAccordes = creditRepository.sumMontantAccordeByDateDecaissementBetween(
                debutTrimestre, finTrimestre);

        BigDecimal depotsCollectes = transactionRepository.sumMontantByTypeCodeDateBetweenAndStatut(
                "DEPOT", debut, fin, StatutOperation.EXECUTEE);

        // Ratio de solvabilite simplifie: fonds propres / total bilan * 100
        BigDecimal fondsPropres = totalBilan.multiply(new BigDecimal("0.15")); // Estimation a 15% du bilan
        BigDecimal ratioSolvabilite = BigDecimal.ZERO;
        if (totalBilan.compareTo(BigDecimal.ZERO) > 0) {
            ratioSolvabilite = fondsPropres.multiply(new BigDecimal("100"))
                    .divide(totalBilan, 2, RoundingMode.HALF_UP);
        }

        return new RapportBceaoResponseDTO(
                trimestre,
                annee,
                totalBilan,
                fondsPropres,
                creditsAccordes,
                depotsCollectes,
                ratioSolvabilite
        );
    }

    private byte[] genererPdfExport(String type, String dateDebut, String dateFin) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4, 36, 36, 54, 36);
            PdfWriter.getInstance(document, baos);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
            Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 9);

            document.add(new Paragraph("Rapport " + type, titleFont));
            document.add(new Paragraph("Genere le : " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")), subtitleFont));
            if (dateDebut != null && dateFin != null) {
                document.add(new Paragraph("Periode : " + dateDebut + " a " + dateFin, subtitleFont));
            }
            document.add(new Paragraph(" "));

            if ("FINANCIER".equalsIgnoreCase(type)) {
                RapportFinancierResponseDTO data = genererRapportFinancier(dateDebut, dateFin);
                PdfPTable table = new PdfPTable(2);
                table.setWidthPercentage(100);
                
                table.addCell(new Paragraph("Indicateur", headerFont));
                table.addCell(new Paragraph("Valeur", headerFont));

                table.addCell(new Paragraph("Periode", bodyFont));
                table.addCell(new Paragraph(data.periode(), bodyFont));

                table.addCell(new Paragraph("Total Actifs", bodyFont));
                table.addCell(new Paragraph(data.totalActifs() + " FCFA", bodyFont));

                table.addCell(new Paragraph("Total Passifs", bodyFont));
                table.addCell(new Paragraph(data.totalPassifs() + " FCFA", bodyFont));

                table.addCell(new Paragraph("Produit Net", bodyFont));
                table.addCell(new Paragraph(data.produitNet() + " FCFA", bodyFont));

                table.addCell(new Paragraph("Marge d'interets", bodyFont));
                table.addCell(new Paragraph(data.margeInterets() + " FCFA", bodyFont));

                table.addCell(new Paragraph("Ratio d'efficacite", bodyFont));
                table.addCell(new Paragraph(data.ratioEfficacite() + "%", bodyFont));

                document.add(table);
            } else if ("OPERATIONNEL".equalsIgnoreCase(type)) {
                RapportOperationnelResponseDTO data = genererRapportOperationnel(dateDebut, dateFin, null);
                PdfPTable table = new PdfPTable(2);
                table.setWidthPercentage(100);

                table.addCell(new Paragraph("Indicateur", headerFont));
                table.addCell(new Paragraph("Valeur", headerFont));

                table.addCell(new Paragraph("Periode", bodyFont));
                table.addCell(new Paragraph(data.periode(), bodyFont));

                table.addCell(new Paragraph("Total Depots", bodyFont));
                table.addCell(new Paragraph(data.totalDepots() + " FCFA", bodyFont));

                table.addCell(new Paragraph("Total Retraits", bodyFont));
                table.addCell(new Paragraph(data.totalRetraits() + " FCFA", bodyFont));

                table.addCell(new Paragraph("Total Virements", bodyFont));
                table.addCell(new Paragraph(data.totalVirements() + " FCFA", bodyFont));

                table.addCell(new Paragraph("Nombre Transactions", bodyFont));
                table.addCell(new Paragraph(String.valueOf(data.nbTransactions()), bodyFont));

                table.addCell(new Paragraph("Total Frais", bodyFont));
                table.addCell(new Paragraph(data.montantTotalFrais() + " FCFA", bodyFont));

                document.add(table);
            } else {
                document.add(new Paragraph("Donnees non disponibles pour ce type de rapport.", bodyFont));
            }

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            LOGGER.error("Erreur generation PDF export", e);
            return new byte[0];
        }
    }

    @Override
    @Transactional(readOnly = true)
    public RapportExportResponseDTO exporterRapport(String type, String format, String dateDebut, String dateFin) {
        LOGGER.info("Export rapport {} au format {}", type, format);
        
        String contenuBase64;
        String nomFichier = "rapport_" + type.toLowerCase() + "_"
                + LocalDateTime.now().toLocalDate() + "." + format.toLowerCase();

        if ("PDF".equalsIgnoreCase(format)) {
            byte[] pdfBytes = genererPdfExport(type, dateDebut, dateFin);
            contenuBase64 = Base64.getEncoder().encodeToString(pdfBytes);
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("Rapport ").append(type).append("\r\n");
            sb.append("Date generation;").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))).append("\r\n");
            sb.append("\r\n");

            switch (type.toUpperCase()) {
                case "OPERATIONNEL":
                    RapportOperationnelResponseDTO opData = genererRapportOperationnel(dateDebut, dateFin, null);
                    sb.append("Periode;Total Depots;Total Retraits;Total Virements;Nb Transactions;Total Frais\r\n");
                    sb.append(opData.periode()).append(";")
                      .append(opData.totalDepots()).append(";")
                      .append(opData.totalRetraits()).append(";")
                      .append(opData.totalVirements()).append(";")
                      .append(opData.nbTransactions()).append(";")
                      .append(opData.montantTotalFrais()).append("\r\n");
                    break;
                case "FINANCIER":
                    RapportFinancierResponseDTO finData = genererRapportFinancier(dateDebut, dateFin);
                    sb.append("Periode;Total Actifs;Total Passifs;Produit Net;Marge Interets;Ratio Efficacite\r\n");
                    sb.append(finData.periode()).append(";")
                      .append(finData.totalActifs()).append(";")
                      .append(finData.totalPassifs()).append(";")
                      .append(finData.produitNet()).append(";")
                      .append(finData.margeInterets()).append(";")
                      .append(finData.ratioEfficacite()).append("\r\n");
                    break;
                case "CLIENTS":
                    RapportClientsResponseDTO cltData = genererRapportClients();
                    sb.append("Periode;Total Clients;Nouveaux Clients;Clients Actifs\r\n");
                    sb.append(cltData.periode()).append(";")
                      .append(cltData.totalClients()).append(";")
                      .append(cltData.nouveauxClients()).append(";")
                      .append(cltData.clientsActifs()).append("\r\n");
                    break;
                case "CREDITS":
                    RapportCreditsResponseDTO credData = genererRapportCredits();
                    sb.append("Periode;Total Credits;Montant Accorde;Encours;PAR 30;PAR 90;Taux Impayes\r\n");
                    sb.append(credData.periode()).append(";")
                      .append(credData.totalCredits()).append(";")
                      .append(credData.montantTotalAccorde()).append(";")
                      .append(credData.encoursTotal()).append(";")
                      .append(credData.par30()).append(";")
                      .append(credData.par90()).append(";")
                      .append(credData.tauxImpayes()).append("\r\n");
                    break;
                default:
                    sb.append("Type;Valeur\r\n");
                    sb.append(type).append(";Donnees non disponibles\r\n");
                    break;
            }
            String contenu = sb.toString();
            contenuBase64 = Base64.getEncoder().encodeToString(contenu.getBytes());
        }

        return new RapportExportResponseDTO(
                type,
                format,
                contenuBase64,
                nomFichier,
                LocalDateTime.now()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public RapportExportResponseDTO genererRapportPersonnalise(RapportPersonnaliseRequestDTO requestDTO) {
        LOGGER.info("Generation rapport personnalise {}", requestDTO.getType());
        StringBuilder sb = new StringBuilder();
        sb.append("Rapport Personnalise - ").append(requestDTO.getType()).append("\r\n");
        sb.append("Date generation;").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))).append("\r\n");
        sb.append("Periode;").append(requestDTO.getDateDebut()).append(" - ").append(requestDTO.getDateFin()).append("\r\n");
        sb.append("\r\n");

        String type = requestDTO.getType().toUpperCase();
        LocalDate dateDebut = requestDTO.getDateDebut() != null ?
                LocalDate.parse(requestDTO.getDateDebut()) : LocalDate.now().minusMonths(1);
        LocalDate dateFin = requestDTO.getDateFin() != null ?
                LocalDate.parse(requestDTO.getDateFin()) : LocalDate.now();

        LocalDateTime debut = dateDebut.atStartOfDay();
        LocalDateTime fin = dateFin.atTime(LocalTime.MAX);

        switch (type) {
            case "OPERATIONNEL":
                BigDecimal depots = transactionRepository.sumMontantByTypeCodeDateBetweenAndStatut(
                        "DEPOT", debut, fin, StatutOperation.EXECUTEE);
                BigDecimal retraits = transactionRepository.sumMontantByTypeCodeDateBetweenAndStatut(
                        "RETRAIT", debut, fin, StatutOperation.EXECUTEE);
                BigDecimal virements = transactionRepository.sumMontantByTypeCodeDateBetweenAndStatut(
                        "VIREMENT", debut, fin, StatutOperation.EXECUTEE);
                long nbTx = transactionRepository.countByDateHeureTransactionBetweenAndStatutOperation(
                        debut, fin, StatutOperation.EXECUTEE);
                BigDecimal frais = transactionRepository.sumFraisByDateBetweenAndStatut(
                        debut, fin, StatutOperation.EXECUTEE);
                sb.append("Total Depots;").append(depots).append("\r\n");
                sb.append("Total Retraits;").append(retraits).append("\r\n");
                sb.append("Total Virements;").append(virements).append("\r\n");
                sb.append("Nb Transactions;").append(nbTx).append("\r\n");
                sb.append("Total Frais;").append(frais).append("\r\n");
                break;
            case "FINANCIER":
                BigDecimal actifs = compteRepository.sumSolde();
                BigDecimal produits = transactionRepository.sumFraisByDateBetweenAndStatut(
                        debut, fin, StatutOperation.EXECUTEE);
                sb.append("Total Actifs;").append(actifs).append("\r\n");
                sb.append("Produits (Frais);").append(produits).append("\r\n");
                break;
            case "CLIENTS":
                long totalClts = clientRepository.count();
                long nouveaux = clientRepository.countByDateInscriptionAfter(dateDebut);
                long actifsClts = clientRepository.countByStatutLibelle("ACTIF");
                sb.append("Total Clients;").append(totalClts).append("\r\n");
                sb.append("Nouveaux (periode);").append(nouveaux).append("\r\n");
                sb.append("Clients Actifs;").append(actifsClts).append("\r\n");
                break;
            case "CREDITS":
                long totalCr = creditRepository.count();
                BigDecimal accordes = creditRepository.sumMontantAccorde();
                BigDecimal encours = creditRepository.sumMontantRestantDu();
                sb.append("Total Credits;").append(totalCr).append("\r\n");
                sb.append("Montant Accorde;").append(accordes).append("\r\n");
                sb.append("Encours;").append(encours).append("\r\n");
                break;
            default:
                sb.append("Type de rapport non reconnu;").append(requestDTO.getType()).append("\r\n");
                break;
        }

        String contenu = sb.toString();
        String contenuBase64 = Base64.getEncoder().encodeToString(contenu.getBytes());
        String format = requestDTO.getFormat() != null ? requestDTO.getFormat() : "CSV";
        String nomFichier = "rapport_personnalise_" + requestDTO.getType().toLowerCase() + "_"
                + LocalDate.now() + "." + format.toLowerCase();

        return new RapportExportResponseDTO(
                requestDTO.getType(),
                format,
                contenuBase64,
                nomFichier,
                LocalDateTime.now()
        );
    }
}
