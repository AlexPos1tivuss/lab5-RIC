
package com.bank.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "deposit_transactions")
public class DepositTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_deposit_id", nullable = false)
    private ClientDeposit clientDeposit;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;
    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;
    
    @Column(nullable = false)
    private LocalDateTime transactionDate;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal balanceAfter;
    
    // Constructors
    public DepositTransaction() {
        this.transactionDate = LocalDateTime.now();
    }
    
    public DepositTransaction(ClientDeposit clientDeposit, TransactionType type, 
                            BigDecimal amount, String description, BigDecimal balanceAfter) {
        this.clientDeposit = clientDeposit;
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.balanceAfter = balanceAfter;
        this.transactionDate = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public ClientDeposit getClientDeposit() { return clientDeposit; }
    public void setClientDeposit(ClientDeposit clientDeposit) { this.clientDeposit = clientDeposit; }
    
    public TransactionType getType() { return type; }
    public void setType(TransactionType type) { this.type = type; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public LocalDateTime getTransactionDate() { return transactionDate; }
    public void setTransactionDate(LocalDateTime transactionDate) { this.transactionDate = transactionDate; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public BigDecimal getBalanceAfter() { return balanceAfter; }
    public void setBalanceAfter(BigDecimal balanceAfter) { this.balanceAfter = balanceAfter; }
    
    public enum TransactionType {
        DEPOSIT, WITHDRAWAL, INTEREST_PAYMENT, INITIAL_DEPOSIT
    }
}
