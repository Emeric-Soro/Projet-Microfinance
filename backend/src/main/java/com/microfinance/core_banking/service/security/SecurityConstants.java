package com.microfinance.core_banking.service.security;

/**
 * Centralized constants for roles and permissions used in @PreAuthorize annotations.
 * Prevents hardcoded string duplication and typos.
 */
public final class SecurityConstants {

    private SecurityConstants() {}

    // ============================================================
    // ROLES
    // ============================================================
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_SUPERVISEUR = "SUPERVISEUR";
    public static final String ROLE_GUICHETIER = "GUICHETIER";
    public static final String ROLE_CLIENT = "CLIENT";
    public static final String ROLE_CHEF_AGENCE = "CHEF_AGENCE";
    public static final String ROLE_MANAGER = "MANAGER";

    // ============================================================
    // PERMISSIONS
    // ============================================================
    public static final String PERM_PAYMENTS_MANAGE = "PAYMENTS_MANAGE";
    public static final String PERM_PAYMENTS_VIEW = "PAYMENTS_VIEW";
    public static final String PERM_ORGANIZATION_MANAGE = "ORGANIZATION_MANAGE";
    public static final String PERM_ORGANIZATION_VIEW = "ORGANIZATION_VIEW";
    public static final String PERM_RISK_MANAGE = "RISK_MANAGE";
    public static final String PERM_RISK_VIEW = "RISK_VIEW";
    public static final String PERM_FEATURE_VIEW = "FEATURE_VIEW";
    public static final String PERM_SAVINGS_MANAGE = "SAVINGS_MANAGE";
    public static final String PERM_SAVINGS_VIEW = "SAVINGS_VIEW";
    public static final String PERM_DIGITAL_MANAGE = "DIGITAL_MANAGE";
    public static final String PERM_DIGITAL_VIEW = "DIGITAL_VIEW";
    public static final String PERM_CREDIT_MANAGE = "CREDIT_MANAGE";
    public static final String PERM_CREDIT_VIEW = "CREDIT_VIEW";
    public static final String PERM_SUPPORT_MANAGE = "SUPPORT_MANAGE";
    public static final String PERM_SUPPORT_VIEW = "SUPPORT_VIEW";
    public static final String PERM_COMPLIANCE_MANAGE = "COMPLIANCE_MANAGE";
    public static final String PERM_COMPLIANCE_VIEW = "COMPLIANCE_VIEW";
    public static final String PERM_VALIDATION_VIEW = "VALIDATION_VIEW";
    public static final String PERM_VALIDATION_DECIDE = "VALIDATION_DECIDE";
    public static final String PERM_TREASURY_MANAGE = "TREASURY_MANAGE";
    public static final String PERM_TREASURY_VIEW = "TREASURY_VIEW";
    public static final String PERM_ACCOUNTING_MANAGE = "ACCOUNTING_MANAGE";
    public static final String PERM_ACCOUNTING_VIEW = "ACCOUNTING_VIEW";
    public static final String PERM_SECURITY_AUDIT_VIEW = "SECURITY_AUDIT_VIEW";
    public static final String PERM_SECURITY_PERMISSION_VIEW = "SECURITY_PERMISSION_VIEW";
    public static final String PERM_SECURITY_PERMISSION_MANAGE = "SECURITY_PERMISSION_MANAGE";

    // Monétique / Cartes
    public static final String PERM_CREATE_CARTE = "CREATE_CARTE";
    public static final String PERM_UPDATE_CARTE = "UPDATE_CARTE";
    public static final String PERM_PAYMENT_CARTE = "PAYMENT_CARTE";

    // Client view
    public static final String PERM_VIEW_CLIENT = "VIEW_CLIENT";

    // Account management
    public static final String PERM_ACCOUNT_VIEW = "ACCOUNT_VIEW";
    public static final String PERM_ACCOUNT_MANAGE = "ACCOUNT_MANAGE";

    // Transaction permissions
    public static final String PERM_TRANSACTION_CREATE = "TRANSACTION_CREATE";
    public static final String PERM_TRANSACTION_VALIDATE = "TRANSACTION_VALIDATE";

    // User management
    public static final String PERM_USER_VIEW = "USER_VIEW";
    public static final String PERM_USER_MANAGE = "USER_MANAGE";

    // Role management
    public static final String PERM_ROLE_MANAGE = "ROLE_MANAGE";

    // Parameter management
    public static final String PERM_PARAMETER_MANAGE = "PARAMETER_MANAGE";

    // Cash desk management
    public static final String PERM_CASH_VIEW = "CASH_VIEW";
    public static final String PERM_CASH_MANAGE = "CASH_MANAGE";

    // Audit
    public static final String PERM_AUDIT_VIEW = "AUDIT_VIEW";

    // ============================================================
    // COMMON AUTHORITY EXPRESSIONS (for @PreAuthorize)
    // ============================================================
    public static final String AUTH_ADMIN = "hasAuthority('" + ROLE_ADMIN + "')";
    public static final String AUTH_ADMIN_SUPERVISEUR = "hasAnyAuthority('" + ROLE_ADMIN + "','" + ROLE_SUPERVISEUR + "')";
    public static final String AUTH_ADMIN_GUICHETIER = "hasAnyAuthority('" + ROLE_ADMIN + "','" + ROLE_GUICHETIER + "')";
    public static final String AUTH_ADMIN_SUPERVISEUR_GUICHETIER = "hasAnyAuthority('" + ROLE_ADMIN + "','" + ROLE_SUPERVISEUR + "','" + ROLE_GUICHETIER + "')";
    public static final String AUTH_MANAGER = "hasAuthority('" + ROLE_MANAGER + "')";

    public static String hasAnyAuthority(String... authorities) {
        return "hasAnyAuthority(" + String.join(",", java.util.stream.Stream.of(authorities).map(a -> "'" + a + "'").toArray(String[]::new)) + ")";
    }

    public static String hasAuthority(String authority) {
        return "hasAuthority('" + authority + "')";
    }
}
