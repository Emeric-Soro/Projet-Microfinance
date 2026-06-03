package com.soutra.microfinance.service.compte;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.soutra.microfinance.entity.*;
import com.soutra.microfinance.repository.compte.CompteRepository;
import com.soutra.microfinance.repository.operation.LigneEcritureRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReleveServiceImpl implements ReleveService {

    private static final int PLAFOnd_JOURS_PDF = 90;
    private static final int PLAFOnd_JOURS_CSV = 365;

    private final CompteRepository compteRepository;
    private final LigneEcritureRepository ligneEcritureRepository;

    @Override
    @Transactional(readOnly = true)
    public byte[] genererReleve(String numCompte, LocalDate du, LocalDate au, ReleveFormat format) {
        if (format == null) {
            throw new IllegalArgumentException("Le format est obligatoire (PDF ou CSV)");
        }

        long joursDemandes = java.time.temporal.ChronoUnit.DAYS.between(du, au);
        if (format == ReleveFormat.PDF && joursDemandes > PLAFOnd_JOURS_PDF) {
            throw new IllegalArgumentException("Le releve PDF est limite a " + PLAFOnd_JOURS_PDF + " jours");
        }
        if (format == ReleveFormat.CSV && joursDemandes > PLAFOnd_JOURS_CSV) {
            throw new IllegalArgumentException("Le releve CSV est limite a " + PLAFOnd_JOURS_CSV + " jours");
        }

        Compte compte = compteRepository.findByNumCompte(numCompte)
                .orElseThrow(() -> new EntityNotFoundException("Compte introuvable: " + numCompte));

        LocalDateTime dateDebut = du.atStartOfDay();
        LocalDateTime dateFin = au.plusDays(1).atStartOfDay();

        List<LigneEcriture> lignes = ligneEcritureRepository
                .findByCompte_IdCompteAndCreatedAtBetweenOrderByCreatedAtDesc(
                        compte.getIdCompte(), dateDebut, dateFin);

        return switch (format) {
            case PDF -> generatePdf(compte, lignes, du, au);
            case CSV -> generateCsv(compte, lignes, du, au);
        };
    }

    private byte[] generatePdf(Compte compte, List<LigneEcriture> lignes, LocalDate du, LocalDate au) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4, 36, 36, 54, 36);
            PdfWriter.getInstance(document, baos);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
            Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 9);

            document.add(new Paragraph("Releve de compte", titleFont));
            document.add(new Paragraph("Compte : " + compte.getNumCompte(), bodyFont));
            document.add(new Paragraph("Du " + du + " au " + au, bodyFont));
            document.add(new Paragraph("Solde actuel : " + compte.getSolde() + " " + compte.getDevise(), bodyFont));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{2, 3, 1.5f, 2, 2.5f});

            addHeaderCell(table, "Date", headerFont);
            addHeaderCell(table, "Reference", headerFont);
            addHeaderCell(table, "Sens", headerFont);
            addHeaderCell(table, "Montant", headerFont);
            addHeaderCell(table, "Type", headerFont);

            BigDecimal totalDebit = BigDecimal.ZERO;
            BigDecimal totalCredit = BigDecimal.ZERO;

            for (LigneEcriture ligne : lignes) {
                String date = ligne.getCreatedAt() != null
                        ? ligne.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                        : "-";
                String ref = ligne.getTransaction() != null
                        ? ligne.getTransaction().getReferenceUnique()
                        : "-";
                String sens = ligne.getSens() != null ? ligne.getSens().name() : "-";
                String montant = ligne.getMontant() != null ? ligne.getMontant().toPlainString() : "0";
                String type = ligne.getTransaction() != null && ligne.getTransaction().getTypeTransaction() != null
                        ? ligne.getTransaction().getTypeTransaction().getLibelle()
                        : "-";

                if (ligne.getSens() == SensEcriture.DEBIT && ligne.getMontant() != null) {
                    totalDebit = totalDebit.add(ligne.getMontant());
                } else if (ligne.getSens() == SensEcriture.CREDIT && ligne.getMontant() != null) {
                    totalCredit = totalCredit.add(ligne.getMontant());
                }

                table.addCell(new PdfPCell(new Phrase(date, bodyFont)));
                table.addCell(new PdfPCell(new Phrase(ref, bodyFont)));
                table.addCell(new PdfPCell(new Phrase(sens, bodyFont)));
                table.addCell(new PdfPCell(new Phrase(montant, bodyFont)));
                table.addCell(new PdfPCell(new Phrase(type, bodyFont)));
            }

            document.add(table);
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Total debite : " + totalDebit + " | Total credite : " + totalCredit, headerFont));

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Erreur lors de la generation du releve PDF", e);
            throw new IllegalStateException("Impossible de generer le releve PDF");
        }
    }

    private byte[] generateCsv(Compte compte, List<LigneEcriture> lignes, LocalDate du, LocalDate au) {
        StringBuilder sb = new StringBuilder();
        sb.append("Date,Reference,Sens,Montant,Type\n");

        for (LigneEcriture ligne : lignes) {
            String date = ligne.getCreatedAt() != null
                    ? ligne.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                    : "";
            String ref = ligne.getTransaction() != null
                    ? escapeCsv(ligne.getTransaction().getReferenceUnique())
                    : "";
            String sens = ligne.getSens() != null ? ligne.getSens().name() : "";
            String montant = ligne.getMontant() != null ? ligne.getMontant().toPlainString() : "0";
            String type = ligne.getTransaction() != null && ligne.getTransaction().getTypeTransaction() != null
                    ? escapeCsv(ligne.getTransaction().getTypeTransaction().getLibelle())
                    : "";

            sb.append(date).append(",")
              .append(ref).append(",")
              .append(sens).append(",")
              .append(montant).append(",")
              .append(type).append("\n");
        }

        return sb.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }

    private void addHeaderCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
