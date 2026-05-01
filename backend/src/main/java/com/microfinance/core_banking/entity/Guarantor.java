package com.microfinance.core_banking.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * Guarantor information for a loan facility.
 * Stores the guarantor's customer reference and their contribution / guarantee amount.
 */
@Entity
@Table(name = "guarantor")
public class Guarantor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "loan_facility_id", nullable = false)
    private Long loanFacilityId;

    @Column(name = "guarantor_customer_id", nullable = false)
    private Long guarantorCustomerId;

    @Column(name = "guarantee_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal guaranteeAmount;

    @Column(name = "guarantee_percentage", nullable = true, precision = 5, scale = 4)
    private BigDecimal guaranteePercentage;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private GuarantorStatus status;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getLoanFacilityId() { return loanFacilityId; }
    public void setLoanFacilityId(Long loanFacilityId) { this.loanFacilityId = loanFacilityId; }

    public Long getGuarantorCustomerId() { return guarantorCustomerId; }
    public void setGuarantorCustomerId(Long guarantorCustomerId) { this.guarantorCustomerId = guarantorCustomerId; }

    public BigDecimal getGuaranteeAmount() { return guaranteeAmount; }
    public void setGuaranteeAmount(BigDecimal guaranteeAmount) { this.guaranteeAmount = guaranteeAmount; }

    public BigDecimal getGuaranteePercentage() { return guaranteePercentage; }
    public void setGuaranteePercentage(BigDecimal guaranteePercentage) { this.guaranteePercentage = guaranteePercentage; }

    public GuarantorStatus getStatus() { return status; }
    public void setStatus(GuarantorStatus status) { this.status = status; }

    public enum GuarantorStatus {
        PENDING,
        APPROVED,
        RELEASED
    }
}
