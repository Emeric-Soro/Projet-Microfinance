package com.soutra.microfinance.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "soutra_notification_preference")
@Getter
@Setter
@NoArgsConstructor
public class NotificationPreference extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pref")
    private Long idPref;

    @Column(name = "id_client", nullable = false, unique = true)
    private Long idClient;

    @Column(name = "push_actif", nullable = false)
    private Boolean pushActif = Boolean.TRUE;

    @Column(name = "sms_actif", nullable = false)
    private Boolean smsActif = Boolean.TRUE;

    @Column(name = "email_actif", nullable = false)
    private Boolean emailActif = Boolean.TRUE;
}
