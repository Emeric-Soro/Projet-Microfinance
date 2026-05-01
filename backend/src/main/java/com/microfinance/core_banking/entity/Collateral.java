package com.microfinance.core_banking.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * Collateral attached to a loan facility.
 */
@Entity
@Table(name = "collateral")
public class Collateral {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "loan_facility_id", nullable = false)
    private Long loanFacilityId;

    @Enumerated(EnumType.STRING)
    @Column(name = "collateral_type", nullable = false)
    private CollateralType collateralType;

    @Column(name = "description")
    private String description;

    @Column(name = "value", nullable = false, precision = 19, scale = 2)
    private BigDecimal value;

    @Enumerated(EnumType.STRING)
    @Column(name = "lien_status", nullable = false)
    private LienStatus lienStatus;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getLoanFacilityId() { return loanFacilityId; }
    public void setLoanFacilityId(Long loanFacilityId) { this.loanFacilityId = loanFacilityId; }

    public CollateralType getCollateralType() { return collateralType; }
    public void setCollateralType(CollateralType collateralType) { this.collateralType = collateralType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getValue() { return value; }
    public void setValue(BigDecimal value) { this.value = value; }

    public LienStatus getLienStatus() { return lienStatus; }
    public void setLienStatus(LienStatus lienStatus) { this.lienStatus = lienStatus; }

    public enum CollateralType {
        REAL_ESTATE,
        VEHICLE,
        SAVINGS_VALUE, // paper/stock value or similar collateral
        OTHER
    }

    public enum LienStatus {
        NOT_LIEN,
        LIEN_ON,
        RELEASED
    }
}
