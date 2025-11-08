
package com.bank.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "client_deposits")
public class ClientDeposit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deposit_product_id", nullable = false)
    private DepositProduct depositProduct;
    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal initialAmount;
    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal currentBalance;
    
    @Column(nullable = false)
    private Integer termMonths;
    
    @Column(nullable = false)
    private LocalDateTime startDate;
    
    @Column(nullable = false)
    private LocalDateTime endDate;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DepositStatus status = DepositStatus.ACTIVE;
    
    @OneToMany(mappedBy = "clientDeposit", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DepositTransaction> transactions;
    
    // Constructors
    public ClientDeposit() {}
    
    public ClientDeposit(Client client, DepositProduct depositProduct, BigDecimal initialAmount, Integer termMonths) {
        this.client = client;
        this.depositProduct = depositProduct;
        this.initialAmount = initialAmount;
        this.currentBalance = initialAmount;
        this.termMonths = termMonths;
        this.startDate = LocalDateTime.now();
        this.endDate = startDate.plusMonths(termMonths);
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }
    
    public DepositProduct getDepositProduct() { return depositProduct; }
    public void setDepositProduct(DepositProduct depositProduct) { this.depositProduct = depositProduct; }
    
    public BigDecimal getInitialAmount() { return initialAmount; }
    public void setInitialAmount(BigDecimal initialAmount) { this.initialAmount = initialAmount; }
    
    public BigDecimal getCurrentBalance() { return currentBalance; }
    public void setCurrentBalance(BigDecimal currentBalance) { this.currentBalance = currentBalance; }
    
    public Integer getTermMonths() { return termMonths; }
    public void setTermMonths(Integer termMonths) { this.termMonths = termMonths; }
    
    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }
    
    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }
    
    public DepositStatus getStatus() { return status; }
    public void setStatus(DepositStatus status) { this.status = status; }
    
    public List<DepositTransaction> getTransactions() { return transactions; }
    public void setTransactions(List<DepositTransaction> transactions) { this.transactions = transactions; }
    
    public enum DepositStatus {
        ACTIVE, CLOSED, MATURED
    }
}
