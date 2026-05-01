package com.microfinance.core_banking.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "permission_securite")
public class PermissionSecurite extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_permission")
    private Long idPermission;

    @Column(name = "code_permission", nullable = false, length = 100, unique = true)
    private String codePermission;

    @Column(name = "libelle_permission", nullable = false, length = 150)
    private String libellePermission;

    @Column(name = "module_code", nullable = false, length = 60)
    private String moduleCode;

    @Column(name = "description_permission", length = 500)
    private String descriptionPermission;

    @Column(name = "actif", nullable = false)
    private Boolean actif = Boolean.TRUE;

    @ManyToMany(mappedBy = "permissions")
    private Set<RoleUtilisateur> roles = new HashSet<>();
}
