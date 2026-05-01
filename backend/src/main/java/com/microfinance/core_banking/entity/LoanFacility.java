package com.microfinance.core_banking.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Represents a credit facility / loan in the core banking domain.
 * This is a high-priority entity to capture lending products, terms and status.
 * Note: customer/product relations are modeled via IDs to avoid tight coupling
 * with existing aggregate roots.
 */
@Entity
@Table(name = "loan_facility")
public class LoanFacility {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Reference to the borrower (customer) by id. Optional foreign-key constraint
    // can be added when Customer entity exists.
    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "product_id", nullable = true)
    private Long productId;

    @Column(name = "principal_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal principalAmount;

    @Column(name = "outstanding_balance", nullable = false, precision = 19, scale = 2)
    private BigDecimal outstandingBalance;

    // Annual interest rate as a percentage (e.g., 12.5)
    @Column(name = "interest_rate", nullable = false, precision = 5, scale = 4)
    private BigDecimal interestRate;

    @Column(name = "term_months", nullable = false)
    private Integer termMonths;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private LoanStatus status;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public BigDecimal getPrincipalAmount() { return principalAmount; }
    public void setPrincipalAmount(BigDecimal principalAmount) { this.principalAmount = principalAmount; }

    public BigDecimal getOutstandingBalance() { return outstandingBalance; }
    public void setOutstandingBalance(BigDecimal outstandingBalance) { this.outstandingBalance = outstandingBalance; }

    public BigDecimal getInterestRate() { return interestRate; }
    public void setInterestRate(BigDecimal interestRate) { this.interestRate = interestRate; }

    public Integer getTermMonths() { return termMonths; }
    public void setTermMonths(Integer termMonths) { this.termMonths = termMonths; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public LoanStatus getStatus() { return status; }
    public void setStatus(LoanStatus status) { this.status = status; }

    public enum LoanStatus {
        APPLICATION_PENDING,
        APPROVED,
        ACTIVE,
        CLOSED,
        REPAID,
        DEFAULTED
    }
}
