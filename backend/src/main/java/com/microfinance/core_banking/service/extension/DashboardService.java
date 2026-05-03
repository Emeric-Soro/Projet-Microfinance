package com.microfinance.core_banking.service.extension;

import com.microfinance.core_banking.entity.Credit;
import com.microfinance.core_banking.entity.DemandeCredit;
import com.microfinance.core_banking.entity.ProduitCredit;
import com.microfinance.core_banking.entity.Transaction;
import com.microfinance.core_banking.repository.client.ClientRepository;
import com.microfinance.core_banking.repository.compte.CompteRepository;
import com.microfinance.core_banking.repository.extension.ActionEnAttenteRepository;
import com.microfinance.core_banking.repository.extension.CreditRepository;
import com.microfinance.core_banking.repository.operation.TransactionRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    private final ClientRepository clientRepository;
    private final CompteRepository compteRepository;
    private final TransactionRepository transactionRepository;
    private final ActionEnAttenteRepository actionEnAttenteRepository;
    private final CreditRepository creditRepository;

    public DashboardService(
            ClientRepository clientRepository,
            CompteRepository compteRepository,
            TransactionRepository transactionRepository,
            ActionEnAttenteRepository actionEnAttenteRepository,
            CreditRepository creditRepository
    ) {
        this.clientRepository = clientRepository;
        this.compteRepository = compteRepository;
        this.transactionRepository = transactionRepository;
        this.actionEnAttenteRepository = actionEnAttenteRepository;
        this.creditRepository = creditRepository;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> buildGeneralDashboard() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("totalClients", clientRepository.count());
        payload.put("totalComptes", compteRepository.count());
        payload.put(
                "operationsJour",
                transactionRepository.findByDateHeureTransactionBetween(startOfDay, endOfDay, PageRequest.of(0, 1))
                        .getTotalElements()
        );
        payload.put("validationsEnAttente", actionEnAttenteRepository.countByStatutIgnoreCase("EN_ATTENTE"));
        payload.put("dernieresOperations", buildLatestOperations());
        payload.put("encoursParProduit", buildOutstandingByProduct());

        return payload;
    }

    private List<Map<String, Object>> buildLatestOperations() {
        return transactionRepository.findAll(PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "dateHeureTransaction")))
                .getContent()
                .stream()
                .map(this::toOperationRow)
                .toList();
    }

    private Map<String, Object> toOperationRow(Transaction transaction) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("reference", transaction.getReferenceUnique());
        row.put(
                "type",
                transaction.getTypeTransaction() != null
                        ? transaction.getTypeTransaction().getLibelle()
                        : "Operation"
        );
        row.put(
                "montant",
                transaction.getMontantGlobal() != null
                        ? transaction.getMontantGlobal().stripTrailingZeros().toPlainString()
                        : "0"
        );
        row.put(
                "statut",
                transaction.getStatutOperation() != null
                        ? transaction.getStatutOperation().name()
                        : "INCONNU"
        );
        return row;
    }

    private List<Map<String, Object>> buildOutstandingByProduct() {
        Map<String, ProductAggregate> aggregates = new LinkedHashMap<>();

        for (Credit credit : creditRepository.findAll()) {
            String productLabel = resolveProductLabel(credit);
            ProductAggregate aggregate = aggregates.computeIfAbsent(productLabel, ignored -> new ProductAggregate());
            aggregate.count++;
            aggregate.outstanding = aggregate.outstanding.add(
                    credit.getCapitalRestantDu() != null ? credit.getCapitalRestantDu() : BigDecimal.ZERO
            );
        }

        List<Map<String, Object>> rows = new ArrayList<>();
        aggregates.entrySet()
                .stream()
                .sorted(Comparator.comparing((Map.Entry<String, ProductAggregate> entry) -> entry.getValue().outstanding).reversed())
                .forEach(entry -> {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("produit", entry.getKey());
                    row.put("nombre", entry.getValue().count);
                    row.put("encours", entry.getValue().outstanding.stripTrailingZeros().toPlainString());
                    rows.add(row);
                });

        return rows;
    }

    private String resolveProductLabel(Credit credit) {
        DemandeCredit demandeCredit = credit.getDemandeCredit();
        if (demandeCredit == null) {
            return "Produit non renseigne";
        }

        ProduitCredit produitCredit = demandeCredit.getProduitCredit();
        if (produitCredit == null || produitCredit.getLibelle() == null || produitCredit.getLibelle().isBlank()) {
            return "Produit non renseigne";
        }

        return produitCredit.getLibelle();
    }

    private static final class ProductAggregate {
        private long count = 0;
        private BigDecimal outstanding = BigDecimal.ZERO;
    }
}
