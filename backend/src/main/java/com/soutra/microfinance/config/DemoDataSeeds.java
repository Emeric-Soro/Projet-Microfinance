package com.soutra.microfinance.config;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Seeds de démonstration pour la plateforme SOUTRA (Microfinance, Côte d'Ivoire).
 * Couvre 150 clients, 25 utilisateurs, 300 comptes, 600 transactions,
 * 80 demandes de crédit, 50 crédits actifs, 400 échéances, 200 notifications,
 * 100 bénéficiaires et 150 documents.
 */
final class DemoDataSeeds {

    static final String DEMO_PASSWORD = "Demo@12345";

    // =========================================================================
    // SECTION 1 : UTILISATEURS STAFF + CLIENTS LIÉS (17 existants inchangés)
    // =========================================================================

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
            new DemoUserSeed("CLI-RDC-0005", "Tshimanga", "Jean", LocalDate.of(1988, 7, 9), "jean.tshimanga@demo.microfin.local", "+243856789012", "Kisangani, Tshopo", "Agriculteur", "Agriculture", "CNI-RDC-2026-0005", "+243856789012", "CLIENT", "EPARGNE", "CD23MB000100000005", new BigDecimal("420000.00")),
            // --- Guichetiers additionnels ---
            new DemoUserSeed("CLI-20260527-0031", "Kouame", "Jean-Pierre", LocalDate.of(1989, 4, 12), "jean.kouame@demo.microfin.local", "+225 05 02 10 10 31", "Cocody, Abidjan", "Guichetier", "Finance", "CNI-CI-2026-0031", "jean.kouame", "GUICHETIER", "EPARGNE", "CI23CB000100000031", new BigDecimal("0.00")),
            new DemoUserSeed("CLI-20260527-0032", "Diallo", "Mamadou", LocalDate.of(1992, 10, 5), "mamadou.diallo@demo.microfin.local", "+225 01 02 10 10 32", "Marcory, Abidjan", "Guichetier", "Finance", "CNI-CI-2026-0032", "mamadou.diallo", "GUICHETIER", "EPARGNE", "CI23CB000100000032", new BigDecimal("0.00"))
    );

    // =========================================================================
    // SECTION 2 : TRANSACTIONS EXISTANTES (inchangées)
    // =========================================================================

    static final List<TransactionSeed> TRANSACTIONS = List.of(
            new TransactionSeed("TRX-DEMO-20260527-0001", "DEPOT", null, "CI23CB000100000005", new BigDecimal("150000.00"), BigDecimal.ZERO, 6, "yao.koffi"),
            new TransactionSeed("TRX-DEMO-20260527-0002", "DEPOT", null, "CI23CB000100000008", new BigDecimal("75000.00"), BigDecimal.ZERO, 5, "jean.kouame"),
            new TransactionSeed("TRX-DEMO-20260527-0003", "RETRAIT", "CI23CB000100000007", null, new BigDecimal("25000.00"), new BigDecimal("250.00"), 4, "mamadou.diallo"),
            new TransactionSeed("TRX-DEMO-20260527-0004", "VIREMENT", "CI23CB000100000009", "CI23CB000100000010", new BigDecimal("60000.00"), new BigDecimal("500.00"), 3, "yao.koffi"),
            new TransactionSeed("TRX-DEMO-20260527-0005", "DEPOT", null, "CI23CB000100000012", new BigDecimal("220000.00"), BigDecimal.ZERO, 2, "jean.kouame"),
            new TransactionSeed("TRX-DEMO-20260527-0006", "DEPOT", null, "CI23CB000100000011", new BigDecimal("350000.00"), BigDecimal.ZERO, 1, "mamadou.diallo"),
            new TransactionSeed("TRX-DEMO-20260527-0007", "RETRAIT", "CI23CB000100000006", null, new BigDecimal("45000.00"), new BigDecimal("450.00"), 1, "yao.koffi"),
            new TransactionSeed("TRX-DEMO-20260527-0008", "VIREMENT", "CI23CB000100000012", "CI23CB000100000005", new BigDecimal("80000.00"), new BigDecimal("800.00"), 0, "jean.kouame"),
            new TransactionSeed("TRX-DEMO-20260527-0009", "DEPOT", null, "CI23CB000100000008", new BigDecimal("125000.00"), BigDecimal.ZERO, 0, "mamadou.diallo"),
            // --- Transactions DRC pour tests mobile ---
            new TransactionSeed("TRX-RDC-20260527-0001", "DEPOT", null, "CD23MB000100000001", new BigDecimal("100000.00"), BigDecimal.ZERO, 3, "demo.admin"),
            new TransactionSeed("TRX-RDC-20260527-0002", "VIREMENT", "CD23MB000100000001", "CD23MB000100000002", new BigDecimal("50000.00"), BigDecimal.ZERO, 2, "demo.admin"),
            new TransactionSeed("TRX-RDC-20260527-0003", "RETRAIT", "CD23MB000100000003", null, new BigDecimal("25000.00"), new BigDecimal("250.00"), 1, "demo.admin")
    );

    // =========================================================================
    // SECTION 3 : CLIENTS PURS — 133 clients sans utilisateur associé
    // CLI-DEMO-0018 à CLI-DEMO-0150
    // =========================================================================

    static final List<DemoClientSeed> CLIENTS = List.of(
        // --- COMMERCANTS (45) ---
        new DemoClientSeed("CLI-DEMO-0018","Camara","Mariam","F",LocalDate.of(1988,5,20),"mariam.camara@email.com","+225 07 18 20 20 18","Marche de Koumassi, Abidjan","Commercante","Commerce de detail","CNI-CI-2026-0018",new BigDecimal("350000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0019","Diallo","Aissatou","F",LocalDate.of(1992,8,14),"aissatou.diallo@email.com","+225 05 19 20 20 19","Yopougon Selmer, Abidjan","Grossiste","Commerce de gros","CNI-CI-2026-0019",new BigDecimal("950000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0020","Kone","Dramane","M",LocalDate.of(1985,3,7),null,"+225 01 20 20 20 20","Adjame, Abidjan","Commercant","Commerce de detail","CNI-CI-2026-0020",new BigDecimal("280000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0021","Coulibaly","Marietou","F",LocalDate.of(1979,11,25),"marietou.coulibaly@email.com","+225 07 21 20 20 21","Plateau, Abidjan","Grossiste","Commerce de gros","CNI-CI-2026-0021",new BigDecimal("1200000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0022","Toure","Seydou","M",LocalDate.of(1990,6,18),null,"+225 05 22 20 20 22","Treichville, Abidjan","Commercant","Commerce de detail","CNI-CI-2026-0022",new BigDecimal("195000"),"ACTIF","EN_ATTENTE","MODERE"),
        new DemoClientSeed("CLI-DEMO-0023","Sanogo","Kadiatou","F",LocalDate.of(1995,2,3),"kadiatou.sanogo@email.com","+225 01 23 20 20 23","Cocody, Abidjan","Commercante","Commerce de detail","CNI-CI-2026-0023",new BigDecimal("320000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0024","Bakayoko","Ibrahim","M",LocalDate.of(1983,9,12),null,"+225 07 24 20 20 24","Bouake Centre, Bouake","Commercant","Commerce de gros","CNI-CI-2026-0024",new BigDecimal("750000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0025","Doumbia","Fatoumata","F",LocalDate.of(1991,4,28),"fatoumata.doumbia@email.com","+225 05 25 20 20 25","Yopougon Niangon, Abidjan","Commercante","Commerce de detail","CNI-CI-2026-0025",new BigDecimal("175000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0026","Keita","Mamadou","M",LocalDate.of(1987,7,15),null,"+225 01 26 20 20 26","Treichville, Abidjan","Commercant","Commerce de detail","CNI-CI-2026-0026",new BigDecimal("230000"),"ACTIF","EN_ATTENTE","MODERE"),
        new DemoClientSeed("CLI-DEMO-0027","Soumahoro","Aminata","F",LocalDate.of(1993,12,9),"aminata.soumahoro@email.com","+225 07 27 20 20 27","Marcory Zone 4, Abidjan","Commercante","Commerce de detail","CNI-CI-2026-0027",new BigDecimal("290000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0028","Sylla","Oumar","M",LocalDate.of(1980,3,22),null,"+225 05 28 20 20 28","Adjame, Abidjan","Grossiste","Commerce de gros","CNI-CI-2026-0028",new BigDecimal("1500000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0029","Fofana","Mariama","F",LocalDate.of(1997,10,5),"mariama.fofana@email.com","+225 01 29 20 20 29","Koumassi, Abidjan","Commercante","Commerce de detail","CNI-CI-2026-0029",new BigDecimal("145000"),"NOUVEAU","BROUILLON","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0030","Sangare","Moussa","M",LocalDate.of(1984,6,19),null,"+225 07 30 20 20 30","Abobo Baoule, Abidjan","Commercant","Commerce de detail","CNI-CI-2026-0030",new BigDecimal("210000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0033","Diarra","Oumou","F",LocalDate.of(1989,1,14),"oumou.diarra@email.com","+225 05 33 20 20 33","Marcory, Abidjan","Commercante","Commerce de detail","CNI-CI-2026-0033",new BigDecimal("265000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0034","Konate","Daouda","M",LocalDate.of(1986,8,27),null,"+225 01 34 20 20 34","Treichville, Abidjan","Commercant","Commerce de gros","CNI-CI-2026-0034",new BigDecimal("870000"),"ACTIF","VALIDE","MODERE"),
        new DemoClientSeed("CLI-DEMO-0035","Kouyate","Salimata","F",LocalDate.of(1994,4,10),"salimata.kouyate@email.com","+225 07 35 20 20 35","Yopougon Siporex, Abidjan","Commercante","Commerce de detail","CNI-CI-2026-0035",new BigDecimal("155000"),"ACTIF","EN_ATTENTE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0036","Coulibaly","Abou","M",LocalDate.of(1981,11,3),null,"+225 05 36 20 20 36","Plateau, Abidjan","Grossiste","Commerce de gros","CNI-CI-2026-0036",new BigDecimal("1800000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0037","Traore","Tenin","F",LocalDate.of(1998,7,22),"tenin.traore@email.com","+225 01 37 20 20 37","Abobo, Abidjan","Commercante","Commerce de detail","CNI-CI-2026-0037",new BigDecimal("120000"),"NOUVEAU","BROUILLON","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0038","Fanny","Adama","M",LocalDate.of(1988,2,16),null,"+225 07 38 20 20 38","Cocody Riviera, Abidjan","Grossiste","Commerce de gros","CNI-CI-2026-0038",new BigDecimal("2000000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0039","Dagnogo","Bintou","F",LocalDate.of(1992,9,8),"bintou.dagnogo@email.com","+225 05 39 20 20 39","Abobo PK18, Abidjan","Commercante","Commerce de detail","CNI-CI-2026-0039",new BigDecimal("185000"),"ACTIF","VALIDE","MODERE"),
        new DemoClientSeed("CLI-DEMO-0040","Dao","Lassine","M",LocalDate.of(1985,5,31),null,"+225 01 40 20 20 40","Bouake Nord, Bouake","Commercant","Commerce de detail","CNI-CI-2026-0040",new BigDecimal("310000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0041","Kacou","Patricia","F",LocalDate.of(1990,12,17),"patricia.kacou@email.com","+225 07 41 20 20 41","Marcory, Abidjan","Commercante","Commerce de detail","CNI-CI-2026-0041",new BigDecimal("240000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0042","Silue","Lacina","M",LocalDate.of(1983,3,5),null,"+225 05 42 20 20 42","Korhogo Centre, Korhogo","Commercant","Commerce de gros","CNI-CI-2026-0042",new BigDecimal("680000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0043","Berte","Rokia","F",LocalDate.of(1996,6,29),"rokia.berte@email.com","+225 01 43 20 20 43","Treichville, Abidjan","Commercante","Commerce de detail","CNI-CI-2026-0043",new BigDecimal("130000"),"ACTIF","EN_ATTENTE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0044","Yeo","Karamoko","M",LocalDate.of(1987,10,13),null,"+225 07 44 20 20 44","Daloa Centre, Daloa","Commercant","Commerce de detail","CNI-CI-2026-0044",new BigDecimal("420000"),"ACTIF","VALIDE","MODERE"),
        new DemoClientSeed("CLI-DEMO-0045","Gbane","Kadja","F",LocalDate.of(1993,2,25),"kadja.gbane@email.com","+225 05 45 20 20 45","Yamoussoukro, Bassam","Commercante","Commerce de detail","CNI-CI-2026-0045",new BigDecimal("195000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0046","N'Dri","Kouassi","M",LocalDate.of(1980,7,7),null,"+225 01 46 20 20 46","Koumassi, Abidjan","Grossiste","Commerce de gros","CNI-CI-2026-0046",new BigDecimal("1100000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0047","Kassi","Amenan","F",LocalDate.of(1991,11,19),"amenan.kassi@email.com","+225 07 47 20 20 47","Yopougon, Abidjan","Commercante","Commerce de detail","CNI-CI-2026-0047",new BigDecimal("175000"),"BLOQUE","REJETE","ELEVE"),
        new DemoClientSeed("CLI-DEMO-0048","Assamoi","Honorat","M",LocalDate.of(1984,4,2),null,"+225 05 48 20 20 48","San Pedro Centre, San Pedro","Commercant","Commerce de gros","CNI-CI-2026-0048",new BigDecimal("590000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0049","Essoua","Akissi","F",LocalDate.of(1995,8,15),"akissi.essoua@email.com","+225 01 49 20 20 49","Abobo, Abidjan","Commercante","Commerce de detail","CNI-CI-2026-0049",new BigDecimal("210000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0050","Gbe","Alain","M",LocalDate.of(1989,1,28),null,"+225 07 50 20 20 50","Treichville, Abidjan","Commercant","Commerce de detail","CNI-CI-2026-0050",new BigDecimal("265000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0051","Brou","Roseline","F",LocalDate.of(1986,5,10),"roseline.brou@email.com","+225 05 51 20 20 51","Marcory, Abidjan","Grossiste","Commerce de gros","CNI-CI-2026-0051",new BigDecimal("780000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0052","Attobra","Gnagnan","M",LocalDate.of(1982,9,23),null,"+225 01 52 20 20 52","Man Centre, Man","Commercant","Commerce de detail","CNI-CI-2026-0052",new BigDecimal("345000"),"ACTIF","VALIDE","MODERE"),
        new DemoClientSeed("CLI-DEMO-0053","Kouakou","Adjoua","F",LocalDate.of(1994,3,6),"adjoua.kouakou@email.com","+225 07 53 20 20 53","Cocody, Abidjan","Commercante","Commerce de detail","CNI-CI-2026-0053",new BigDecimal("230000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0054","Kouassi","Firmin","M",LocalDate.of(1988,7,18),null,"+225 05 54 20 20 54","Gagnoa Centre, Gagnoa","Commercant","Commerce de gros","CNI-CI-2026-0054",new BigDecimal("920000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0055","Kouame","Benie","F",LocalDate.of(1997,12,1),"benie.kouame@email.com","+225 01 55 20 20 55","Abidjan Plateau, Abidjan","Commercante","Commerce de detail","CNI-CI-2026-0055",new BigDecimal("155000"),"NOUVEAU","BROUILLON","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0056","Yacé","René","M",LocalDate.of(1981,4,14),null,"+225 07 56 20 20 56","Treichville, Abidjan","Grossiste","Commerce de gros","CNI-CI-2026-0056",new BigDecimal("1600000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0057","Mensah","Carine","F",LocalDate.of(1990,8,27),"carine.mensah@email.com","+225 05 57 20 20 57","Koumassi, Abidjan","Commercante","Commerce de detail","CNI-CI-2026-0057",new BigDecimal("195000"),"ACTIF","EN_ATTENTE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0058","Gbagbo","Narcisse","M",LocalDate.of(1985,2,9),null,"+225 01 58 20 20 58","Yopougon, Abidjan","Commercant","Commerce de detail","CNI-CI-2026-0058",new BigDecimal("310000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0059","Obou","Viviane","F",LocalDate.of(1993,6,22),"viviane.obou@email.com","+225 07 59 20 20 59","Abobo, Abidjan","Commercante","Commerce de detail","CNI-CI-2026-0059",new BigDecimal("175000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0060","Blé","Constant","M",LocalDate.of(1987,11,4),null,"+225 05 60 20 20 60","Daloa, Tazibouo","Grossiste","Commerce de gros","CNI-CI-2026-0060",new BigDecimal("1050000"),"ACTIF","VALIDE","MODERE"),
        new DemoClientSeed("CLI-DEMO-0061","Loglo","Henriette","F",LocalDate.of(1991,3,17),"henriette.loglo@email.com","+225 01 61 20 20 61","Marcory, Abidjan","Commercante","Commerce de detail","CNI-CI-2026-0061",new BigDecimal("220000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0062","Kassi","Amon","M",LocalDate.of(1984,7,30),null,"+225 07 62 20 20 62","Cocody, Abidjan","Commercant","Commerce de detail","CNI-CI-2026-0062",new BigDecimal("380000"),"ACTIF","VALIDE","FAIBLE"),
        // --- SALARIES (30) ---
        new DemoClientSeed("CLI-DEMO-0063","Aka","Christophe","M",LocalDate.of(1986,2,14),"christophe.aka@email.com","+225 05 63 20 20 63","Cocody, Abidjan","Fonctionnaire","Services publics","CNI-CI-2026-0063",new BigDecimal("350000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0064","Akoua","Stephanie","F",LocalDate.of(1990,5,27),null,"+225 01 64 20 20 64","Marcory, Abidjan","Comptable","Finance","CNI-CI-2026-0064",new BigDecimal("420000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0065","Diomande","Lacina","M",LocalDate.of(1983,9,10),"lacina.diomande@email.com","+225 07 65 20 20 65","Bouake Centre, Bouake","Ingenieur","Informatique","CNI-CI-2026-0065",new BigDecimal("650000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0066","Bah","Fatoumata","F",LocalDate.of(1994,1,23),null,"+225 05 66 20 20 66","Treichville, Abidjan","Infirmiere","Sante","CNI-CI-2026-0066",new BigDecimal("290000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0067","Cisse","Ladji","M",LocalDate.of(1988,6,5),"ladji.cisse@email.com","+225 01 67 20 20 67","Abobo, Abidjan","Technicien","Informatique","CNI-CI-2026-0067",new BigDecimal("380000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0068","Coulibaly","Tenin","F",LocalDate.of(1992,10,18),null,"+225 07 68 20 20 68","Yopougon, Abidjan","Secretaire","Services","CNI-CI-2026-0068",new BigDecimal("220000"),"ACTIF","EN_ATTENTE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0069","Tuo","Noufou","M",LocalDate.of(1980,3,1),"noufou.tuo@email.com","+225 05 69 20 20 69","Korhogo Centre, Korhogo","Fonctionnaire","Services publics","CNI-CI-2026-0069",new BigDecimal("450000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0070","Bamba","Mariam","F",LocalDate.of(1995,7,14),null,"+225 01 70 20 20 70","Cocody, Abidjan","Juriste","Services","CNI-CI-2026-0070",new BigDecimal("500000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0071","N'Goran","Koffi","M",LocalDate.of(1985,11,27),"koffi.ngoran@email.com","+225 07 71 20 20 71","Plateau, Abidjan","Fonctionnaire","Services publics","CNI-CI-2026-0071",new BigDecimal("370000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0072","Kouakou","Angeline","F",LocalDate.of(1991,4,9),null,"+225 05 72 20 20 72","Marcory, Abidjan","Comptable","Finance","CNI-CI-2026-0072",new BigDecimal("310000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0073","Ouedraogo","Mamadou","M",LocalDate.of(1987,8,22),"mamadou.ouedraogo@email.com","+225 01 73 20 20 73","Abobo, Abidjan","Fonctionnaire","Services publics","CNI-CI-2026-0073",new BigDecimal("400000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0074","Savadogo","Rasmata","F",LocalDate.of(1993,12,5),null,"+225 07 74 20 20 74","Treichville, Abidjan","Infirmiere","Sante","CNI-CI-2026-0074",new BigDecimal("275000"),"ACTIF","VALIDE","MODERE"),
        new DemoClientSeed("CLI-DEMO-0075","Dembele","Seydou","M",LocalDate.of(1982,5,18),"seydou.dembele@email.com","+225 05 75 20 20 75","Bouake, Quartier Air France","Ingenieur","Informatique","CNI-CI-2026-0075",new BigDecimal("720000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0076","Sanogo","Aminata","F",LocalDate.of(1996,9,1),null,"+225 01 76 20 20 76","Yamoussoukro Centre, Yamoussoukro","Pharmacienne","Sante","CNI-CI-2026-0076",new BigDecimal("580000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0077","Kone","Pascal","M",LocalDate.of(1984,1,14),"pascal.kone@email.com","+225 07 77 20 20 77","Cocody Riviera, Abidjan","Fonctionnaire","Services publics","CNI-CI-2026-0077",new BigDecimal("480000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0078","Diallo","Maimouna","F",LocalDate.of(1989,6,27),null,"+225 05 78 20 20 78","Marcory, Abidjan","Secretaire","Services","CNI-CI-2026-0078",new BigDecimal("240000"),"ACTIF","EN_ATTENTE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0079","Fofana","Aboubacar","M",LocalDate.of(1986,10,10),"aboubacar.fofana@email.com","+225 01 79 20 20 79","Abobo, Abidjan","Technicien","Informatique","CNI-CI-2026-0079",new BigDecimal("360000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0080","Koita","Hawa","F",LocalDate.of(1994,3,23),null,"+225 07 80 20 20 80","Yopougon, Abidjan","Comptable","Finance","CNI-CI-2026-0080",new BigDecimal("330000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0081","Konate","Lamine","M",LocalDate.of(1981,7,5),"lamine.konate@email.com","+225 05 81 20 20 81","Koumassi, Abidjan","Fonctionnaire","Services publics","CNI-CI-2026-0081",new BigDecimal("440000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0082","Bah","Konimba","F",LocalDate.of(1990,11,18),null,"+225 01 82 20 20 82","Treichville, Abidjan","Pharmacienne","Sante","CNI-CI-2026-0082",new BigDecimal("610000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0083","Soumahoro","Bafing","M",LocalDate.of(1987,4,1),"bafing.soumahoro@email.com","+225 07 83 20 20 83","Bouake, Aire France","Ingenieur","Informatique","CNI-CI-2026-0083",new BigDecimal("800000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0084","Coulibaly","Nafi","F",LocalDate.of(1993,8,14),null,"+225 05 84 20 20 84","Daloa Centre, Daloa","Fonctionnaire","Services publics","CNI-CI-2026-0084",new BigDecimal("280000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0085","Bakayoko","Arouna","M",LocalDate.of(1983,12,27),"arouna.bakayoko@email.com","+225 01 85 20 20 85","Korhogo, Tioribougou","Technicien","Informatique","CNI-CI-2026-0085",new BigDecimal("410000"),"ACTIF","VALIDE","MODERE"),
        new DemoClientSeed("CLI-DEMO-0086","Sylla","Kadiatou","F",LocalDate.of(1995,5,10),null,"+225 07 86 20 20 86","Cocody, Abidjan","Juriste","Services","CNI-CI-2026-0086",new BigDecimal("550000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0087","Berete","Souleymane","M",LocalDate.of(1988,9,22),"souleymane.berete@email.com","+225 05 87 20 20 87","Plateau, Abidjan","Fonctionnaire","Services publics","CNI-CI-2026-0087",new BigDecimal("470000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0088","Kacou","Henriette","F",LocalDate.of(1991,2,5),null,"+225 01 88 20 20 88","Marcory, Abidjan","Infirmiere","Sante","CNI-CI-2026-0088",new BigDecimal("295000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0089","Tchamba","Felix","M",LocalDate.of(1984,6,18),"felix.tchamba@email.com","+225 07 89 20 20 89","San Pedro, Bardot","Fonctionnaire","Services publics","CNI-CI-2026-0089",new BigDecimal("380000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0090","Camara","Nafi","F",LocalDate.of(1996,10,1),null,"+225 05 90 20 20 90","Abobo, Abidjan","Secretaire","Services","CNI-CI-2026-0090",new BigDecimal("210000"),"ACTIF","EN_ATTENTE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0091","Doumbia","Mamadi","M",LocalDate.of(1982,3,14),"mamadi.doumbia@email.com","+225 01 91 20 20 91","Yopougon, Abidjan","Fonctionnaire","Services publics","CNI-CI-2026-0091",new BigDecimal("460000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0092","Kone","Veronique","F",LocalDate.of(1990,7,27),null,"+225 07 92 20 20 92","Treichville, Abidjan","Comptable","Finance","CNI-CI-2026-0092",new BigDecimal("390000"),"ACTIF","VALIDE","FAIBLE"),
        // --- ARTISANS (25) ---
        new DemoClientSeed("CLI-DEMO-0093","Konan","Moussa","M",LocalDate.of(1985,4,10),"moussa.konan@email.com","+225 05 93 20 20 93","Yopougon, Abidjan","Menuisier","Artisanat","CNI-CI-2026-0093",new BigDecimal("185000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0094","Toure","Kadidja","F",LocalDate.of(1991,8,23),null,"+225 01 94 20 20 94","Treichville, Abidjan","Coiffeuse","Artisanat","CNI-CI-2026-0094",new BigDecimal("120000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0095","Sanogo","Ladji","M",LocalDate.of(1987,12,6),"ladji.sanogo@email.com","+225 07 95 20 20 95","Abobo, Abidjan","Soudeur","Artisanat","CNI-CI-2026-0095",new BigDecimal("210000"),"ACTIF","VALIDE","MODERE"),
        new DemoClientSeed("CLI-DEMO-0096","Dembele","Aminata","F",LocalDate.of(1994,3,19),null,"+225 05 96 20 20 96","Koumassi, Abidjan","Couturiere","Artisanat","CNI-CI-2026-0096",new BigDecimal("145000"),"ACTIF","EN_ATTENTE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0097","Kone","Drissa","M",LocalDate.of(1983,7,2),"drissa.kone@email.com","+225 01 97 20 20 97","Man Centre, Man","Forgeron","Artisanat","CNI-CI-2026-0097",new BigDecimal("175000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0098","Coulibaly","Kadja","F",LocalDate.of(1997,11,15),null,"+225 07 98 20 20 98","Daloa, Tazibouo","Coiffeuse","Artisanat","CNI-CI-2026-0098",new BigDecimal("100000"),"NOUVEAU","BROUILLON","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0099","Fofana","Brehima","M",LocalDate.of(1988,4,28),"brehima.fofana@email.com","+225 05 99 20 20 99","Bouake, Centre-ville","Menuisier","Artisanat","CNI-CI-2026-0099",new BigDecimal("220000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0100","Bakayoko","Ramatou","F",LocalDate.of(1992,8,10),null,"+225 01 00 21 21 00","Yopougon, Abidjan","Couturiere","Artisanat","CNI-CI-2026-0100",new BigDecimal("130000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0101","Traore","Yaya","M",LocalDate.of(1986,1,23),"yaya.traore@email.com","+225 07 01 21 21 01","Korhogo, Quartier Commerce","Menuisier","Artisanat","CNI-CI-2026-0101",new BigDecimal("240000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0102","Cissé","Salimata","F",LocalDate.of(1995,5,6),null,"+225 05 02 21 21 02","Treichville, Abidjan","Coiffeuse","Artisanat","CNI-CI-2026-0102",new BigDecimal("115000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0103","Doumbia","Sekou","M",LocalDate.of(1982,9,19),"sekou.doumbia@email.com","+225 01 03 21 21 03","Abobo, Abidjan","Plombier","Artisanat","CNI-CI-2026-0103",new BigDecimal("280000"),"ACTIF","VALIDE","MODERE"),
        new DemoClientSeed("CLI-DEMO-0104","Keita","Awa","F",LocalDate.of(1990,2,2),null,"+225 07 04 21 21 04","Marcory, Abidjan","Couturiere","Artisanat","CNI-CI-2026-0104",new BigDecimal("160000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0105","Sangare","Moussa","M",LocalDate.of(1985,6,15),"moussa.sangare@email.com","+225 05 05 21 21 05","Gagnoa, Soleil","Electricien","Artisanat","CNI-CI-2026-0105",new BigDecimal("310000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0106","Diallo","Fatoumata","F",LocalDate.of(1993,10,28),null,"+225 01 06 21 21 06","San Pedro, Bardot","Coiffeuse","Artisanat","CNI-CI-2026-0106",new BigDecimal("110000"),"ACTIF","EN_ATTENTE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0107","Camara","Lanciné","M",LocalDate.of(1987,3,11),"lancine.camara@email.com","+225 07 07 21 21 07","Yopougon, Abidjan","Menuisier","Artisanat","CNI-CI-2026-0107",new BigDecimal("195000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0108","Coulibaly","Mariam","F",LocalDate.of(1991,7,24),null,"+225 05 08 21 21 08","Abobo, Abidjan","Couturiere","Artisanat","CNI-CI-2026-0108",new BigDecimal("125000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0109","Sylla","Cheick","M",LocalDate.of(1984,11,6),"cheick.sylla@email.com","+225 01 09 21 21 09","Koumassi, Abidjan","Forgeron","Artisanat","CNI-CI-2026-0109",new BigDecimal("170000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0110","Kone","Rokia","F",LocalDate.of(1998,3,19),null,"+225 07 10 21 21 10","Treichville, Abidjan","Coiffeuse","Artisanat","CNI-CI-2026-0110",new BigDecimal("95000"),"NOUVEAU","BROUILLON","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0111","Soumahoro","Seydou","M",LocalDate.of(1986,7,2),"seydou.soumahoro@email.com","+225 05 11 21 21 11","Bouake Nord, Bouake","Soudeur","Artisanat","CNI-CI-2026-0111",new BigDecimal("230000"),"ACTIF","VALIDE","MODERE"),
        new DemoClientSeed("CLI-DEMO-0112","Dagnogo","Bintou","F",LocalDate.of(1994,11,15),null,"+225 01 12 21 21 12","Marcory, Abidjan","Couturiere","Artisanat","CNI-CI-2026-0112",new BigDecimal("140000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0113","Diarra","Bakary","M",LocalDate.of(1983,4,28),"bakary.diarra@email.com","+225 07 13 21 21 13","Yopougon, Abidjan","Electricien","Artisanat","CNI-CI-2026-0113",new BigDecimal("295000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0114","Kouyate","Tenin","F",LocalDate.of(1992,8,10),null,"+225 05 14 21 21 14","Cocody, Abidjan","Couturiere","Artisanat","CNI-CI-2026-0114",new BigDecimal("155000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0115","Keita","Bamba","M",LocalDate.of(1988,12,23),"bamba.keita@email.com","+225 01 15 21 21 15","Abobo PK18, Abidjan","Plombier","Artisanat","CNI-CI-2026-0115",new BigDecimal("260000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0116","Fanny","Gnagna","F",LocalDate.of(1996,5,5),null,"+225 07 16 21 21 16","Gagnoa, Centre-ville","Coiffeuse","Artisanat","CNI-CI-2026-0116",new BigDecimal("108000"),"ACTIF","EN_ATTENTE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0117","Sacko","Ibrahim","M",LocalDate.of(1981,9,18),"ibrahim.sacko@email.com","+225 05 17 21 21 17","Treichville, Abidjan","Menuisier","Artisanat","CNI-CI-2026-0117",new BigDecimal("320000"),"ACTIF","VALIDE","FAIBLE"),
        // --- AGRICULTEURS (20) ---
        new DemoClientSeed("CLI-DEMO-0118","Coulibaly","Gnamba","M",LocalDate.of(1975,3,2),"gnamba.coulibaly@email.com","+225 01 18 21 21 18","Korhogo, Tioribougou","Agriculteur","Agriculture","CNI-CI-2026-0118",new BigDecimal("250000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0119","Traore","Mariama","F",LocalDate.of(1983,7,15),null,"+225 07 19 21 21 19","Man, Centre-ville","Agricultrice","Agriculture","CNI-CI-2026-0119",new BigDecimal("180000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0120","Silue","Tenin","F",LocalDate.of(1989,11,28),"tenin.silue@email.com","+225 05 20 21 21 20","Korhogo, Quartier Commerce","Agricultrice","Agriculture","CNI-CI-2026-0120",new BigDecimal("140000"),"ACTIF","EN_ATTENTE","MODERE"),
        new DemoClientSeed("CLI-DEMO-0121","Konate","Inza","M",LocalDate.of(1977,4,10),null,"+225 01 21 21 21 21","Daloa, Tazibouo","Agriculteur","Agriculture","CNI-CI-2026-0121",new BigDecimal("320000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0122","Bakayoko","Salamata","F",LocalDate.of(1985,8,23),"salamata.bakayoko@email.com","+225 07 22 21 21 22","Gagnoa, Centre-ville","Agricultrice","Agriculture","CNI-CI-2026-0122",new BigDecimal("190000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0123","Kone","Sibiri","M",LocalDate.of(1972,12,5),null,"+225 05 23 21 21 23","Bouake, Bouake Nord","Agriculteur","Agriculture","CNI-CI-2026-0123",new BigDecimal("460000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0124","Diallo","Awa","F",LocalDate.of(1990,3,18),"awa.diallo@email.com","+225 01 24 21 21 24","Man, Quartier Commerce","Agricultrice","Agriculture","CNI-CI-2026-0124",new BigDecimal("155000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0125","N'Dri","Kobenan","M",LocalDate.of(1980,7,1),null,"+225 07 25 21 21 25","San Pedro, Bardot","Agriculteur","Agriculture","CNI-CI-2026-0125",new BigDecimal("280000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0126","Camara","Djénéba","F",LocalDate.of(1987,10,14),"djeneba.camara@email.com","+225 05 26 21 21 26","Korhogo, Centre-ville","Agricultrice","Agriculture","CNI-CI-2026-0126",new BigDecimal("160000"),"ACTIF","VALIDE","MODERE"),
        new DemoClientSeed("CLI-DEMO-0127","Soumahoro","Ali","M",LocalDate.of(1979,2,27),null,"+225 01 27 21 21 27","Daloa, Centre-ville","Agriculteur","Agriculture","CNI-CI-2026-0127",new BigDecimal("380000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0128","Tuo","Kafigui","F",LocalDate.of(1984,6,10),"kafigui.tuo@email.com","+225 07 28 21 21 28","Man, Quartier Commerce","Agricultrice","Agriculture","CNI-CI-2026-0128",new BigDecimal("130000"),"ACTIF","EN_ATTENTE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0129","Coulibaly","Zakaria","M",LocalDate.of(1976,10,23),null,"+225 05 29 21 21 29","Bouake, Centre-ville","Agriculteur","Agriculture","CNI-CI-2026-0129",new BigDecimal("495000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0130","Bamba","Bintou","F",LocalDate.of(1991,3,5),"bintou.bamba@email.com","+225 01 30 21 21 30","Korhogo, Tioribougou","Agricultrice","Agriculture","CNI-CI-2026-0130",new BigDecimal("120000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0131","Sangare","Drissa","M",LocalDate.of(1983,7,18),null,"+225 07 31 21 21 31","Daloa, Quartier Commerce","Agriculteur","Agriculture","CNI-CI-2026-0131",new BigDecimal("310000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0132","Coulibaly","Djenabou","F",LocalDate.of(1993,11,30),"djenabou.coulibaly@email.com","+225 05 32 21 21 32","Man, Libreville","Agricultrice","Agriculture","CNI-CI-2026-0132",new BigDecimal("145000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0133","Fofana","Siaka","M",LocalDate.of(1974,4,12),null,"+225 01 33 21 21 33","Gagnoa, Soleil","Agriculteur","Agriculture","CNI-CI-2026-0133",new BigDecimal("410000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0134","Diabate","Mawa","F",LocalDate.of(1988,8,25),"mawa.diabate@email.com","+225 07 34 21 21 34","Bouake, Quartier Air France","Agricultrice","Agriculture","CNI-CI-2026-0134",new BigDecimal("170000"),"ACTIF","EN_ATTENTE","MODERE"),
        new DemoClientSeed("CLI-DEMO-0135","Kone","Dramane","M",LocalDate.of(1982,1,8),null,"+225 05 35 21 21 35","Korhogo, Tioribougou","Agriculteur","Agriculture","CNI-CI-2026-0135",new BigDecimal("355000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0136","Traore","Aminata","F",LocalDate.of(1995,5,21),"aminata.traore@email.com","+225 01 36 21 21 36","Man, Centre-ville","Agricultrice","Agriculture","CNI-CI-2026-0136",new BigDecimal("125000"),"NOUVEAU","BROUILLON","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0137","Sanogo","Adama","M",LocalDate.of(1970,9,3),null,"+225 07 37 21 21 37","Daloa, Tazibouo","Agriculteur","Agriculture","CNI-CI-2026-0137",new BigDecimal("480000"),"ACTIF","VALIDE","FAIBLE"),
        // --- TRANSPORTEURS (15) ---
        new DemoClientSeed("CLI-DEMO-0138","Ouattara","Kassoum","M",LocalDate.of(1984,2,14),"kassoum.ouattara@email.com","+225 05 38 21 21 38","Yopougon, Abidjan","Chauffeur taxi","Transport","CNI-CI-2026-0138",new BigDecimal("220000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0139","Kone","Tenin","F",LocalDate.of(1990,6,27),null,"+225 01 39 21 21 39","Abobo, Abidjan","Vendeuse bord route","Transport","CNI-CI-2026-0139",new BigDecimal("110000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0140","Diallo","Oumar","M",LocalDate.of(1979,10,10),"oumar.diallo@email.com","+225 07 40 21 21 40","Treichville, Abidjan","Chauffeur camion","Transport","CNI-CI-2026-0140",new BigDecimal("480000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0141","Coulibaly","Fatou","F",LocalDate.of(1987,2,23),null,"+225 05 41 21 21 41","Marcory, Abidjan","Conductrice moto","Transport","CNI-CI-2026-0141",new BigDecimal("150000"),"ACTIF","EN_ATTENTE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0142","Traore","Moussa","M",LocalDate.of(1983,6,5),"moussa.traore@email.com","+225 01 42 21 21 42","Bouake, Centre-ville","Chauffeur taxi","Transport","CNI-CI-2026-0142",new BigDecimal("270000"),"ACTIF","VALIDE","MODERE"),
        new DemoClientSeed("CLI-DEMO-0143","Konate","Kadidja","F",LocalDate.of(1995,10,18),null,"+225 07 43 21 21 43","Korhogo, Quartier Commerce","Vendeuse marche","Transport","CNI-CI-2026-0143",new BigDecimal("130000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0144","Bakayoko","Mamadou","M",LocalDate.of(1981,2,1),"mamadou.bakayoko@email.com","+225 05 44 21 21 44","Yopougon, Abidjan","Chauffeur camion","Transport","CNI-CI-2026-0144",new BigDecimal("560000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0145","Camara","Nankama","F",LocalDate.of(1992,6,14),null,"+225 01 45 21 21 45","Daloa, Centre-ville","Conductrice moto","Transport","CNI-CI-2026-0145",new BigDecimal("160000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0146","Fofana","Seydou","M",LocalDate.of(1985,10,27),"seydou.fofana@email.com","+225 07 46 21 21 46","Abobo, Abidjan","Chauffeur taxi","Transport","CNI-CI-2026-0146",new BigDecimal("240000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0147","Soumahoro","Amara","M",LocalDate.of(1977,3,9),null,"+225 05 47 21 21 47","Marcory, Abidjan","Chauffeur camion","Transport","CNI-CI-2026-0147",new BigDecimal("650000"),"ACTIF","VALIDE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0148","N'Goran","Ekoua","F",LocalDate.of(1993,7,22),"ekoua.ngoran@email.com","+225 01 48 21 21 48","Treichville, Abidjan","Conductrice moto","Transport","CNI-CI-2026-0148",new BigDecimal("140000"),"ACTIF","EN_ATTENTE","FAIBLE"),
        new DemoClientSeed("CLI-DEMO-0149","Diarrassouba","Issa","M",LocalDate.of(1989,11,4),null,"+225 07 49 21 21 49","Yopougon, Abidjan","Chauffeur taxi","Transport","CNI-CI-2026-0149",new BigDecimal("195000"),"ACTIF","VALIDE","FAIBLE"),
        // --- RESTAURATEURS (10) ---
        new DemoClientSeed("CLI-DEMO-0150","Assi","Marie-Chantal","F",LocalDate.of(1982,4,17),"marie.assi@email.com","+225 05 50 21 21 50","Cocody, Abidjan","Restauratrice","Restauration","CNI-CI-2026-0150",new BigDecimal("430000"),"ACTIF","VALIDE","FAIBLE")
    );

    // =========================================================================
    // SECTION 4 : SEEDS CRÉDITS — 80 demandes + 50 crédits actifs
    // Format : codeClient | produitCode | montant | dureeMois | objet | dateDemande | statutDemande | statutCredit (null si non décaissé)
    // =========================================================================

    static final List<DemoCreditSeed> CREDIT_SEEDS = List.of(
        // APPROUVEES / DECAISSEES (30 demandes → crédits actifs)
        new DemoCreditSeed("CLI-DEMO-0018","MC-COMMERCE",new BigDecimal("1200000"),12,"Achat de marchandise pour boutique","2025-02-10","APPROUVEE","EN_COURS"),
        new DemoCreditSeed("CLI-DEMO-0019","MC-COMMERCE",new BigDecimal("2000000"),12,"Extension stock grossiste","2025-01-20","APPROUVEE","EN_COURS"),
        new DemoCreditSeed("CLI-DEMO-0021","MC-COMMERCE",new BigDecimal("1500000"),12,"Financement stock boutique principale","2025-03-05","APPROUVEE","EN_COURS"),
        new DemoCreditSeed("CLI-DEMO-0023","MC-COMMERCE",new BigDecimal("800000"),6,"Achat marchandise saison","2025-04-01","APPROUVEE","EN_COURS"),
        new DemoCreditSeed("CLI-DEMO-0024","MC-COMMERCE",new BigDecimal("1800000"),12,"Extension commerce Bouake","2025-02-15","APPROUVEE","EN_RETARD"),
        new DemoCreditSeed("CLI-DEMO-0028","MC-COMMERCE",new BigDecimal("3000000"),18,"Stock grossiste centre commercial","2025-01-10","APPROUVEE","EN_COURS"),
        new DemoCreditSeed("CLI-DEMO-0030","MC-COMMERCE",new BigDecimal("600000"),6,"Marchandise boutique Abobo","2025-05-10","APPROUVEE","EN_COURS"),
        new DemoCreditSeed("CLI-DEMO-0034","MC-COMMERCE",new BigDecimal("2500000"),18,"Financement gros stock","2025-01-15","APPROUVEE","EN_RETARD"),
        new DemoCreditSeed("CLI-DEMO-0036","MC-COMMERCE",new BigDecimal("3500000"),24,"Investissement commerce Plateau","2024-11-01","APPROUVEE","SOLDE"),
        new DemoCreditSeed("CLI-DEMO-0040","MC-COMMERCE",new BigDecimal("700000"),6,"Restockage boutique Bouake","2025-06-01","APPROUVEE","EN_COURS"),
        new DemoCreditSeed("CLI-DEMO-0042","MC-COMMERCE",new BigDecimal("1300000"),12,"Commerce detaillant Korhogo","2025-03-20","APPROUVEE","EN_COURS"),
        new DemoCreditSeed("CLI-DEMO-0044","MC-COMMERCE",new BigDecimal("900000"),6,"Financement fete de fin annee","2025-04-15","APPROUVEE","EN_COURS"),
        new DemoCreditSeed("CLI-DEMO-0046","MC-COMMERCE",new BigDecimal("2200000"),12,"Stock grossiste Koumassi","2025-02-28","APPROUVEE","EN_RETARD"),
        new DemoCreditSeed("CLI-DEMO-0048","MC-COMMERCE",new BigDecimal("1000000"),6,"Commerce San Pedro","2025-05-20","APPROUVEE","EN_COURS"),
        new DemoCreditSeed("CLI-DEMO-0050","MC-COMMERCE",new BigDecimal("500000"),6,"Petite boutique Treichville","2025-06-15","APPROUVEE","EN_COURS"),
        // AGRICULTURE (15 crédits)
        new DemoCreditSeed("CLI-DEMO-0118","MC-AGRICULTURE",new BigDecimal("3000000"),24,"Campagne agricole cacao 2025","2025-01-05","APPROUVEE","EN_COURS"),
        new DemoCreditSeed("CLI-DEMO-0119","MC-AGRICULTURE",new BigDecimal("1500000"),18,"Culture maraichage Man","2025-02-01","APPROUVEE","EN_COURS"),
        new DemoCreditSeed("CLI-DEMO-0121","MC-AGRICULTURE",new BigDecimal("4000000"),24,"Exploitation palmier a huile","2024-12-01","APPROUVEE","SOLDE"),
        new DemoCreditSeed("CLI-DEMO-0123","MC-AGRICULTURE",new BigDecimal("5000000"),24,"Achat intrants agricoles","2025-01-20","APPROUVEE","EN_COURS"),
        new DemoCreditSeed("CLI-DEMO-0125","MC-AGRICULTURE",new BigDecimal("2500000"),18,"Amenagement plantation San Pedro","2025-03-10","APPROUVEE","EN_RETARD"),
        new DemoCreditSeed("CLI-DEMO-0127","MC-AGRICULTURE",new BigDecimal("3500000"),24,"Exploitation hevea Daloa","2024-11-15","APPROUVEE","SOUFFRANCE"),
        new DemoCreditSeed("CLI-DEMO-0129","MC-AGRICULTURE",new BigDecimal("2000000"),12,"Culture vivrier Bouake","2025-04-05","APPROUVEE","EN_COURS"),
        new DemoCreditSeed("CLI-DEMO-0131","MC-AGRICULTURE",new BigDecimal("1800000"),18,"Materiel agricole Daloa","2025-02-20","APPROUVEE","EN_COURS"),
        new DemoCreditSeed("CLI-DEMO-0133","MC-AGRICULTURE",new BigDecimal("2800000"),24,"Campagne anacarde Gagnoa","2025-01-10","APPROUVEE","EN_RETARD"),
        new DemoCreditSeed("CLI-DEMO-0135","MC-AGRICULTURE",new BigDecimal("1200000"),12,"Culture maraichere Korhogo","2025-05-01","APPROUVEE","EN_COURS"),
        // PRET-SALARIE (10 crédits)
        new DemoCreditSeed("CLI-DEMO-0063","PRET-SALARIE",new BigDecimal("3000000"),36,"Construction maison familiale","2025-01-20","APPROUVEE","EN_COURS"),
        new DemoCreditSeed("CLI-DEMO-0065","PRET-SALARIE",new BigDecimal("5000000"),36,"Achat vehicule de service","2024-12-10","APPROUVEE","EN_COURS"),
        new DemoCreditSeed("CLI-DEMO-0067","PRET-SALARIE",new BigDecimal("2500000"),24,"Frais scolarite enfants","2025-02-15","APPROUVEE","EN_COURS"),
        new DemoCreditSeed("CLI-DEMO-0069","PRET-SALARIE",new BigDecimal("4000000"),36,"Acquisition terrain Korhogo","2025-01-05","APPROUVEE","EN_RETARD"),
        new DemoCreditSeed("CLI-DEMO-0071","PRET-SALARIE",new BigDecimal("3500000"),36,"Renovation domicile Plateau","2025-03-01","APPROUVEE","EN_COURS"),
        new DemoCreditSeed("CLI-DEMO-0073","PRET-SALARIE",new BigDecimal("2000000"),24,"Equipement maison","2025-04-10","APPROUVEE","EN_COURS"),
        new DemoCreditSeed("CLI-DEMO-0075","PRET-SALARIE",new BigDecimal("6000000"),36,"Construction maison Bouake","2024-11-20","APPROUVEE","SOUFFRANCE"),
        new DemoCreditSeed("CLI-DEMO-0077","PRET-SALARIE",new BigDecimal("4500000"),36,"Achat vehicule","2024-12-15","APPROUVEE","EN_COURS"),
        new DemoCreditSeed("CLI-DEMO-0079","PRET-SALARIE",new BigDecimal("1500000"),18,"Consommation personnelle","2025-05-20","APPROUVEE","EN_COURS"),
        new DemoCreditSeed("CLI-DEMO-0081","PRET-SALARIE",new BigDecimal("3000000"),30,"Investissement immobilier","2025-01-30","APPROUVEE","EN_COURS"),
        // DEMANDES EN COURS D'ETUDE (20 demandes sans crédit)
        new DemoCreditSeed("CLI-DEMO-0020","MC-COMMERCE",new BigDecimal("500000"),6,"Stock boutique Adjame","2025-06-10","EN_ETUDE",null),
        new DemoCreditSeed("CLI-DEMO-0022","MC-COMMERCE",new BigDecimal("700000"),6,"Marchandise Treichville","2025-06-20","EN_ETUDE",null),
        new DemoCreditSeed("CLI-DEMO-0025","MC-COMMERCE",new BigDecimal("300000"),3,"Stock saisonnier","2025-06-25","EN_ETUDE",null),
        new DemoCreditSeed("CLI-DEMO-0027","MC-COMMERCE",new BigDecimal("600000"),6,"Boutique Marcory","2025-06-15","EN_ETUDE",null),
        new DemoCreditSeed("CLI-DEMO-0033","MC-COMMERCE",new BigDecimal("400000"),6,"Fonds de roulement","2025-06-22","EN_ETUDE",null),
        new DemoCreditSeed("CLI-DEMO-0064","PRET-SALARIE",new BigDecimal("1500000"),18,"Equipement bureau domicile","2025-06-12","EN_ETUDE",null),
        new DemoCreditSeed("CLI-DEMO-0066","PRET-SALARIE",new BigDecimal("2000000"),24,"Frais medicaux famille","2025-06-18","EN_ETUDE",null),
        new DemoCreditSeed("CLI-DEMO-0068","PRET-SALARIE",new BigDecimal("1000000"),12,"Formation professionnelle","2025-06-20","EN_ETUDE",null),
        new DemoCreditSeed("CLI-DEMO-0094","MC-AGRICULTURE",new BigDecimal("800000"),12,"Materiel coiffure professionnel","2025-06-08","EN_ETUDE",null),
        new DemoCreditSeed("CLI-DEMO-0096","MC-AGRICULTURE",new BigDecimal("500000"),6,"Machines couture","2025-06-14","EN_ETUDE",null),
        // DEMANDES REJETEES (15)
        new DemoCreditSeed("CLI-DEMO-0026","MC-COMMERCE",new BigDecimal("1500000"),12,"Investissement commerce Treichville","2025-03-15","REJETEE",null),
        new DemoCreditSeed("CLI-DEMO-0035","MC-COMMERCE",new BigDecimal("800000"),6,"Stock boutique Yopougon","2025-04-10","REJETEE",null),
        new DemoCreditSeed("CLI-DEMO-0037","MC-COMMERCE",new BigDecimal("600000"),6,"Marchandise Abobo","2025-02-20","REJETEE",null),
        new DemoCreditSeed("CLI-DEMO-0043","MC-COMMERCE",new BigDecimal("400000"),3,"Fonds roulement Treichville","2025-05-05","REJETEE",null),
        new DemoCreditSeed("CLI-DEMO-0047","MC-COMMERCE",new BigDecimal("1000000"),6,"Extension boutique","2025-01-25","REJETEE",null),
        new DemoCreditSeed("CLI-DEMO-0070","PRET-SALARIE",new BigDecimal("3000000"),24,"Achat terrain","2025-03-20","REJETEE",null),
        new DemoCreditSeed("CLI-DEMO-0072","PRET-SALARIE",new BigDecimal("2000000"),18,"Renovation maison","2025-02-10","REJETEE",null),
        new DemoCreditSeed("CLI-DEMO-0074","PRET-SALARIE",new BigDecimal("1500000"),12,"Depenses personnelles","2025-04-20","REJETEE",null),
        new DemoCreditSeed("CLI-DEMO-0120","MC-AGRICULTURE",new BigDecimal("500000"),6,"Achat semences Korhogo","2025-05-10","REJETEE",null),
        new DemoCreditSeed("CLI-DEMO-0128","MC-AGRICULTURE",new BigDecimal("700000"),6,"Irrigation Man","2025-03-05","REJETEE",null),
        new DemoCreditSeed("CLI-DEMO-0093","MC-COMMERCE",new BigDecimal("600000"),6,"Achat outils menuiserie","2025-04-15","REJETEE",null),
        new DemoCreditSeed("CLI-DEMO-0095","MC-COMMERCE",new BigDecimal("800000"),6,"Materiel soudure","2025-03-25","REJETEE",null),
        new DemoCreditSeed("CLI-DEMO-0097","MC-COMMERCE",new BigDecimal("400000"),3,"Outillage forge","2025-05-15","REJETEE",null),
        new DemoCreditSeed("CLI-DEMO-0138","MC-COMMERCE",new BigDecimal("700000"),6,"Achat moto taxi","2025-02-28","REJETEE",null),
        new DemoCreditSeed("CLI-DEMO-0140","MC-COMMERCE",new BigDecimal("1200000"),12,"Camion livraison","2025-01-30","REJETEE",null),
        // DEMANDES EN_ATTENTE (15)
        new DemoCreditSeed("CLI-DEMO-0029","MC-COMMERCE",new BigDecimal("200000"),3,"Premiere demande boutique","2025-06-28","EN_ATTENTE",null),
        new DemoCreditSeed("CLI-DEMO-0055","MC-COMMERCE",new BigDecimal("150000"),3,"Stock debut activite","2025-06-29","EN_ATTENTE",null),
        new DemoCreditSeed("CLI-DEMO-0098","MC-COMMERCE",new BigDecimal("100000"),3,"Premier credit coiffure","2025-06-30","EN_ATTENTE",null),
        new DemoCreditSeed("CLI-DEMO-0110","MC-COMMERCE",new BigDecimal("120000"),3,"Materiel coiffure initial","2025-06-29","EN_ATTENTE",null),
        new DemoCreditSeed("CLI-DEMO-0136","MC-AGRICULTURE",new BigDecimal("300000"),6,"Semences premiere campagne","2025-06-28","EN_ATTENTE",null),
        new DemoCreditSeed("CLI-DEMO-0082","PRET-SALARIE",new BigDecimal("1000000"),12,"Urgence medicale","2025-06-27","EN_ATTENTE",null),
        new DemoCreditSeed("CLI-DEMO-0084","PRET-SALARIE",new BigDecimal("800000"),12,"Formation specialisee","2025-06-26","EN_ATTENTE",null),
        new DemoCreditSeed("CLI-DEMO-0086","PRET-SALARIE",new BigDecimal("1500000"),18,"Achat equipement maison","2025-06-25","EN_ATTENTE",null),
        new DemoCreditSeed("CLI-DEMO-0088","PRET-SALARIE",new BigDecimal("600000"),6,"Frais medicaux","2025-06-27","EN_ATTENTE",null),
        new DemoCreditSeed("CLI-DEMO-0090","PRET-SALARIE",new BigDecimal("500000"),6,"Besoins urgents famille","2025-06-28","EN_ATTENTE",null),
        new DemoCreditSeed("CLI-DEMO-0141","MC-COMMERCE",new BigDecimal("200000"),3,"Achat moto","2025-06-26","EN_ATTENTE",null),
        new DemoCreditSeed("CLI-DEMO-0143","MC-COMMERCE",new BigDecimal("150000"),3,"Capital commerce initial","2025-06-29","EN_ATTENTE",null),
        new DemoCreditSeed("CLI-DEMO-0145","MC-COMMERCE",new BigDecimal("250000"),3,"Achat moto transport","2025-06-28","EN_ATTENTE",null),
        new DemoCreditSeed("CLI-DEMO-0116","MC-COMMERCE",new BigDecimal("150000"),3,"Materiel coiffure","2025-06-30","EN_ATTENTE",null),
        new DemoCreditSeed("CLI-DEMO-0112","MC-COMMERCE",new BigDecimal("200000"),3,"Machines a coudre","2025-06-29","EN_ATTENTE",null)
    );

    // =========================================================================
    // RECORDS
    // =========================================================================

    private DemoDataSeeds() {
    }

    // Record pour utilisateur+client+compte existant (inchangé)
    record DemoUserSeed(String codeClient, String nom, String prenom, LocalDate dateNaissance, String email,
                        String telephone, String adresse, String profession, String secteurActivite,
                        String numeroPieceIdentite, String login, String roleCode, String typeCompte,
                        String accountNumber, BigDecimal soldeInitial) {
    }

    // Record pour transaction existante (inchangé)
    record TransactionSeed(String referenceUnique, String typeCode, String sourceAccountNumber,
                           String destinationAccountNumber, BigDecimal montant, BigDecimal frais, int daysAgo,
                           String initiatorLogin) {
    }

    // Record client pur (sans utilisateur)
    record DemoClientSeed(
            String codeClient,
            String nom,
            String prenom,
            String sexe,
            LocalDate dateNaissance,
            String email,         // nullable
            String telephone,
            String adresse,
            String profession,
            String secteurActivite,
            String numeroPieceIdentite,
            BigDecimal revenuMensuel,
            String statutClientCode,  // ACTIF | NOUVEAU | BLOQUE
            String statutKyc,         // VALIDE | EN_ATTENTE | BROUILLON | REJETE | A_REVOIR
            String niveauRisque       // FAIBLE | MODERE | ELEVE | CRITIQUE
    ) {}

    // Record demande + crédit
    record DemoCreditSeed(
            String codeClient,
            String produitCode,       // MC-COMMERCE | MC-AGRICULTURE | PRET-SALARIE
            BigDecimal montantDemande,
            int dureeMois,
            String objetCredit,
            String dateDemande,       // yyyy-MM-dd
            String statutDemande,     // EN_ATTENTE | EN_ETUDE | APPROUVEE | REJETEE
            String statutCredit       // null si pas décaissé, sinon EN_COURS | EN_RETARD | SOLDE | SOUFFRANCE
    ) {}
}
