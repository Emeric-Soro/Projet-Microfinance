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
@Table(name = "soutra_client_blacklist")
public class ClientBlacklist extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_blacklist")
    private Long idBlacklist;

    @OneToOne
    @JoinColumn(name = "id_client", nullable = false, unique = true)
    private Client client;

    @Column(nullable = false, length = 50)
    private String motif;

    @Column(nullable = false, length = 500)
    private String details;

    @Column(name = "date_blacklist", nullable = false)
    private LocalDateTime dateBlacklist = LocalDateTime.now();

    @Column(name = "blackliste_par", nullable = false, length = 120)
    private String blacklistePar;
}
