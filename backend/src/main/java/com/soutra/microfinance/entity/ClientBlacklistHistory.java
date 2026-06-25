package com.soutra.microfinance.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "soutra_client_blacklist_history")
public class ClientBlacklistHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_history")
    private Long idHistory;

    @Column(name = "id_client", nullable = false)
    private Long idClient;

    @Column(nullable = false, length = 30)
    private String action; // AJOUT, RETRAIT

    @Column(name = "client_nom", nullable = false, length = 100)
    private String clientNom;

    @Column(name = "client_prenom", nullable = false, length = 100)
    private String clientPrenom;

    @Column(name = "numero_client", nullable = false, length = 50)
    private String numeroClient;

    @Column(length = 50)
    private String motif;

    @Column(length = 500)
    private String details;

    @Column(name = "date_action", nullable = false)
    private LocalDateTime dateAction = LocalDateTime.now();

    @Column(nullable = false, length = 120)
    private String operateur;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
