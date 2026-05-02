package com.microfinance.core_banking.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * Beneficiary for a loan facility payments routing or officer handover.
 */
@Entity
@Table(name = "beneficiary")
public class Beneficiary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "loan_facility_id", nullable = false)
    private Long loanFacilityId;

    @Column(name = "beneficiary_account", nullable = false)
    private String beneficiaryAccount;

    @Column(name = "beneficiary_name", nullable = false)
    private String beneficiaryName;

    @Column(name = "share_ratio", precision = 5, scale = 4)
    private BigDecimal share;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getLoanFacilityId() { return loanFacilityId; }
    public void setLoanFacilityId(Long loanFacilityId) { this.loanFacilityId = loanFacilityId; }

    public String getBeneficiaryAccount() { return beneficiaryAccount; }
    public void setBeneficiaryAccount(String beneficiaryAccount) { this.beneficiaryAccount = beneficiaryAccount; }

    public String getBeneficiaryName() { return beneficiaryName; }
    public void setBeneficiaryName(String beneficiaryName) { this.beneficiaryName = beneficiaryName; }

    public BigDecimal getShare() { return share; }
    public void setShare(BigDecimal share) { this.share = share; }
}
