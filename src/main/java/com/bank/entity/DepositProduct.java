
package com.bank.entity;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "deposit_products")
public class DepositProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Название депозита не может быть пустым")
    @Column(nullable = false)
    private String name;
    
    @DecimalMin(value = "0.01", message = "Процентная ставка должна быть больше 0")
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal interestRate;
    
    @Min(value = 1, message = "Минимальный срок должен быть не менее 1 месяца")
    @Column(nullable = false)
    private Integer minTermMonths;
    
    @Min(value = 1, message = "Максимальный срок должен быть не менее 1 месяца")
    @Column(nullable = false)
    private Integer maxTermMonths;
    
    @DecimalMin(value = "1.00", message = "Минимальная сумма должна быть не менее 1 рубля")
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal minAmount;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false)
    private Boolean active = true;
    
    @OneToMany(mappedBy = "depositProduct", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ClientDeposit> clientDeposits;
    
    // Constructors
    public DepositProduct() {}
    
    public DepositProduct(String name, BigDecimal interestRate, Integer minTermMonths, 
                         Integer maxTermMonths, BigDecimal minAmount, String description) {
        this.name = name;
        this.interestRate = interestRate;
        this.minTermMonths = minTermMonths;
        this.maxTermMonths = maxTermMonths;
        this.minAmount = minAmount;
        this.description = description;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public BigDecimal getInterestRate() { return interestRate; }
    public void setInterestRate(BigDecimal interestRate) { this.interestRate = interestRate; }
    
    public Integer getMinTermMonths() { return minTermMonths; }
    public void setMinTermMonths(Integer minTermMonths) { this.minTermMonths = minTermMonths; }
    
    public Integer getMaxTermMonths() { return maxTermMonths; }
    public void setMaxTermMonths(Integer maxTermMonths) { this.maxTermMonths = maxTermMonths; }
    
    public BigDecimal getMinAmount() { return minAmount; }
    public void setMinAmount(BigDecimal minAmount) { this.minAmount = minAmount; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
    
    public List<ClientDeposit> getClientDeposits() { return clientDeposits; }
    public void setClientDeposits(List<ClientDeposit> clientDeposits) { this.clientDeposits = clientDeposits; }
}
