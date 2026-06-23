package com.soutra.microfinance.config;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

final class DemoDataSeeds {

    static final String DEMO_PASSWORD = "Demo@12345";
    static final List<DemoUserSeed> USERS = List.of(
            new DemoUserSeed("CLI-DEMO-0001", "Kouadio", "Awa", LocalDate.of(1988, 3, 14), "demo.admin@microfin.local", "+225 07 01 10 10 01", "Cocody, Abidjan", "Commercante", "Commerce de detail", "CNI-CI-2026-0001", "demo.admin", "ADMIN", "EPARGNE", "CI23CB000100000001", new BigDecimal("750000.00")),
            new DemoUserSeed("CLI-20260527-0002", "Koffi", "Yao", LocalDate.of(1991, 8, 7), "yao.koffi@demo.microfin.local", "+225 05 02 10 10 02", "Plateau, Abidjan", "Agent administratif", "Services", "CNI-CI-2026-0002", "yao.koffi", "GUICHETIER", "COURANT", "CI23CB000100000002", new BigDecimal("450000.00")),
            new DemoUserSeed("CLI-20260527-0003", "Konan", "Adjoua", LocalDate.of(1985, 12, 21), "adjoua.konan@demo.microfin.local", "+225 01 03 10 10 03", "Bouake, Air France", "Analyste credit", "Finance", "CNI-CI-2026-0003", "adjoua.konan", "AGENT_CREDIT", "EPARGNE", "CI23CB000100000003", new BigDecimal("620000.00")),
            new DemoUserSeed("CLI-20260527-0004", "N'Guessan", "Serge", LocalDate.of(1982, 5, 9), "serge.nguessan@demo.microfin.local", "+225 07 04 10 10 04", "Marcory, Abidjan", "Chef d'agence", "Management", "CNI-CI-2026-0004", "serge.nguessan", "CHEF_AGENCE", "COURANT", "CI23CB000100000004", new BigDecimal("950000.00")),
            new DemoUserSeed("CLI-20260527-0005", "Bamba", "Fatoumata", LocalDate.of(1994, 2, 18), "fatoumata.bamba@demo.microfin.local", "+225 05 05 10 10 05", "Yopougon, Abidjan", "Restauratrice", "Restauration", "CNI-CI-2026-0005", "fatoumata.bamba", "CLIENT", "EPARGNE", "CI23CB000100000005", new BigDecimal("180000.00")),
            new DemoUserSeed("CLI-20260527-0006", "Ouattara", "Idrissa", LocalDate.of(1990, 10, 30), "idrissa.ouattara@demo.microfin.local", "+225 01 06 10 10 06", "Korhogo, Quartier Commerce", "Transporteur", "Transport", "CNI-CI-2026-0006", "idrissa.ouattara", "CLIENT", "EPARGNE", "CI23CB000100000006", new BigDecimal("125000.00")),
            new DemoUserSeed("CLI-20260527-0007", "Diabate", "Aminata", LocalDate.of(1996, 7, 3), "aminata.diabate@demo.microfin.local", "+225 07 07 10 10 07", "Treichville, Abidjan", "Couturiere", "Artisanat", "CNI-CI-2026-0007", "aminata.diabate", "CLIENT", "COURANT", "CI23CB000100000007", new BigDecimal("340000.00")),
            new DemoUserSeed("CLI-20260527-0008", "Soro", "Mireille", LocalDate.of(1989, 11, 12), "mireille.soro@demo.microfin.local", "+225 05 08 10 10 08", "Daloa, Tazibouo", "Enseignante", "Education", "CNI-CI-2026-0008", "mireille.soro", "CLIENT", "EPARGNE", "CI23CB000100000008", new BigDecimal("275000.00")),
            new DemoUserSeed("CLI-20260527-0009", "Coulibaly", "Yacouba", LocalDate.of(1987, 4, 26), "yacouba.coulibaly@demo.microfin.local", "+225 01 09 10 10 09", "Man, Libreville", "Agriculteur", "Agriculture", "CNI-CI-2026-0009", "yacouba.coulibaly", "CLIENT", "COURANT", "CI23CB000100000009", new BigDecimal("410000.00")),
            new DemoUserSeed("CLI-20260527-0010", "Traore", "Affoue", LocalDate.of(1993, 1, 5), "affoue.traore@demo.microfin.local", "+225 07 10 10 10 10", "San Pedro, Bardot", "Coiffeuse", "Services", "CNI-CI-2026-0010", "affoue.traore", "CLIENT", "EPARGNE", "CI23CB000100000010", new BigDecimal("155000.00")),
            new DemoUserSeed("CLI-20260527-0011", "Gnahore", "Ange", LocalDate.of(1992, 9, 17), "ange.gnahore@demo.microfin.local", "+225 05 11 10 10 11", "Gagnoa, Soleil", "Technicien", "Informatique", "CNI-CI-2026-0011", "ange.gnahore", "CLIENT", "EPARGNE", "CI23CB000100000011", new BigDecimal("210000.00")),
            new DemoUserSeed("CLI-20260527-0012", "Toure", "Nadege", LocalDate.of(1986, 6, 23), "nadege.toure@demo.microfin.local", "+225 01 12 10 10 12", "Abobo, Abidjan", "Grossiste", "Commerce", "CNI-CI-2026-0012", "nadege.toure", "CLIENT", "COURANT", "CI23CB000100000012", new BigDecimal("390000.00")),

            // --- Users DRC (+243) pour tests mobile Flutter ---
            new DemoUserSeed("CLI-RDC-0001", "Lukusa", "Patrick", LocalDate.of(1990, 5, 12), "patrick.lukusa@demo.microfin.local", "+243812345678", "Limbete, Kinshasa", "Commercant", "Commerce", "CNI-RDC-2026-0001", "+243812345678", "CLIENT", "EPARGNE", "CD23MB000100000001", new BigDecimal("500000.00")),
            new DemoUserSeed("CLI-RDC-0002", "Mbuyi", "Cecile", LocalDate.of(1993, 8, 25), "cecile.mbuyi@demo.microfin.local", "+243823456789", "Lubumbashi, Kampemba", "Enseignante", "Education", "CNI-RDC-2026-0002", "+243823456789", "CLIENT", "COURANT", "CD23MB000100000002", new BigDecimal("350000.00")),
            new DemoUserSeed("CLI-RDC-0003", "Kabongo", "David", LocalDate.of(1985, 12, 3), "david.kabongo@demo.microfin.local", "+243834567890", "Goma, Birere", "Transporteur", "Transport", "CNI-RDC-2026-0003", "+243834567890", "CLIENT", "EPARGNE", "CD23MB000100000003", new BigDecimal("275000.00")),
            new DemoUserSeed("CLI-RDC-0004", "Ngoy", "Marie", LocalDate.of(1991, 3, 17), "marie.ngoy@demo.microfin.local", "+243845678901", "Matadi, Congo", "Restauratrice", "Restauration", "CNI-RDC-2026-0004", "+243845678901", "CLIENT", "COURANT", "CD23MB000100000004", new BigDecimal("180000.00")),
            new DemoUserSeed("CLI-RDC-0005", "Tshimanga", "Jean", LocalDate.of(1988, 7, 9), "jean.tshimanga@demo.microfin.local", "+243856789012", "Kisangani, Tshopo", "Agriculteur", "Agriculture", "CNI-RDC-2026-0005", "+243856789012", "CLIENT", "EPARGNE", "CD23MB000100000005", new BigDecimal("420000.00"))
    );
    static final List<TransactionSeed> TRANSACTIONS = List.of(
            new TransactionSeed("TRX-DEMO-20260527-0001", "DEPOT", null, "CI23CB000100000005", new BigDecimal("50000.00"), BigDecimal.ZERO, 6),
            new TransactionSeed("TRX-DEMO-20260527-0002", "DEPOT", null, "CI23CB000100000008", new BigDecimal("75000.00"), BigDecimal.ZERO, 5),
            new TransactionSeed("TRX-DEMO-20260527-0003", "RETRAIT", "CI23CB000100000007", null, new BigDecimal("25000.00"), new BigDecimal("250.00"), 4),
            new TransactionSeed("TRX-DEMO-20260527-0004", "VIREMENT", "CI23CB000100000009", "CI23CB000100000010", new BigDecimal("60000.00"), new BigDecimal("500.00"), 3),
            new TransactionSeed("TRX-DEMO-20260527-0005", "DEPOT", null, "CI23CB000100000012", new BigDecimal("120000.00"), BigDecimal.ZERO, 2),

            // --- Transactions DRC pour tests mobile ---
            new TransactionSeed("TRX-RDC-20260527-0001", "DEPOT", null, "CD23MB000100000001", new BigDecimal("100000.00"), BigDecimal.ZERO, 3),
            new TransactionSeed("TRX-RDC-20260527-0002", "VIREMENT", "CD23MB000100000001", "CD23MB000100000002", new BigDecimal("50000.00"), BigDecimal.ZERO, 2),
            new TransactionSeed("TRX-RDC-20260527-0003", "RETRAIT", "CD23MB000100000003", null, new BigDecimal("25000.00"), new BigDecimal("250.00"), 1)
    );

    private DemoDataSeeds() {
    }

    record DemoUserSeed(String codeClient, String nom, String prenom, LocalDate dateNaissance, String email,
                        String telephone, String adresse, String profession, String secteurActivite,
                        String numeroPieceIdentite, String login, String roleCode, String typeCompte,
                        String accountNumber, BigDecimal soldeInitial) {
    }

    record TransactionSeed(String referenceUnique, String typeCode, String sourceAccountNumber,
                           String destinationAccountNumber, BigDecimal montant, BigDecimal frais, int daysAgo) {
    }
}
