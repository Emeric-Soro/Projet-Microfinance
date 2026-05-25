package com.soutra.microfinance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "soutra_system_audit_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_action", nullable = false)
    private LocalDateTime dateAction;

    @Column(name = "utilisateur", nullable = false, length = 100)
    private String utilisateur;

    @Column(name = "action", nullable = false, length = 100)
    private String action;

    @Column(name = "ressource", length = 100)
    private String ressource;

    @Column(name = "adresse_ip", length = 45)
    private String adresseIp;

    @Column(name = "methode", length = 255)
    private String methode;

    @Column(name = "statut", nullable = false, length = 20)
    private String statut;

    @Column(name = "message_erreur", length = 500)
    private String messageErreur;
}
