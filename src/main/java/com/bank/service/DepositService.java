
package com.bank.service;

import com.bank.entity.*;
import com.bank.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DepositService {
    
    @Autowired
    private ClientRepository clientRepository;
    
    @Autowired
    private DepositProductRepository depositProductRepository;
    
    @Autowired
    private ClientDepositRepository clientDepositRepository;
    
    @Autowired
    private DepositTransactionRepository transactionRepository;
    
    // Базовые методы (минимум 4)
    
    public ClientDeposit openDeposit(Long clientId, Long productId, BigDecimal amount, Integer termMonths) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Клиент не найден"));
        
        DepositProduct product = depositProductRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Продукт не найден"));
        
        if (amount.compareTo(product.getMinAmount()) < 0) {
            throw new RuntimeException("Сумма меньше минимальной для данного продукта");
        }
        
        if (termMonths < product.getMinTermMonths() || termMonths > product.getMaxTermMonths()) {
            throw new RuntimeException("Срок депозита не соответствует условиям продукта");
        }
        
        ClientDeposit deposit = new ClientDeposit(client, product, amount, termMonths);
        deposit = clientDepositRepository.save(deposit);
        
        // Создаем первую транзакцию
        DepositTransaction initialTransaction = new DepositTransaction(
                deposit, 
                DepositTransaction.TransactionType.INITIAL_DEPOSIT,
                amount,
                "Первоначальное размещение депозита",
                amount
        );
        transactionRepository.save(initialTransaction);
        
        return deposit;
    }
    
    public BigDecimal calculateBalance(Long depositId, LocalDateTime date) {
        ClientDeposit deposit = clientDepositRepository.findById(depositId)
                .orElseThrow(() -> new RuntimeException("Депозит не найден"));
        
        if (date.isBefore(deposit.getStartDate())) {
            return BigDecimal.ZERO;
        }
        
        long daysBetween = ChronoUnit.DAYS.between(deposit.getStartDate(), date);
        BigDecimal dailyRate = deposit.getDepositProduct().getInterestRate()
                .divide(new BigDecimal("365"), 10, RoundingMode.HALF_UP)
                .divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP);
        
        BigDecimal interest = deposit.getInitialAmount()
                .multiply(dailyRate)
                .multiply(new BigDecimal(daysBetween))
                .setScale(2, RoundingMode.HALF_UP);
        
        return deposit.getCurrentBalance().add(interest);
    }
    
    public BigDecimal calculateInterestForPeriod(Long depositId, LocalDateTime startDate, LocalDateTime endDate) {
        ClientDeposit deposit = clientDepositRepository.findById(depositId)
                .orElseThrow(() -> new RuntimeException("Депозит не найден"));
        
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        if (daysBetween <= 0) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal dailyRate = deposit.getDepositProduct().getInterestRate()
                .divide(new BigDecimal("365"), 10, RoundingMode.HALF_UP)
                .divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP);
        
        return deposit.getCurrentBalance()
                .multiply(dailyRate)
                .multiply(new BigDecimal(daysBetween))
                .setScale(2, RoundingMode.HALF_UP);
    }
    
    public void withdrawInterest(Long depositId, BigDecimal amount) {
        ClientDeposit deposit = clientDepositRepository.findById(depositId)
                .orElseThrow(() -> new RuntimeException("Депозит не найден"));
        
        BigDecimal availableInterest = calculateBalance(depositId, LocalDateTime.now())
                .subtract(deposit.getCurrentBalance());
        
        if (amount.compareTo(availableInterest) > 0) {
            throw new RuntimeException("Недостаточно начисленных процентов для снятия");
        }
        
        DepositTransaction transaction = new DepositTransaction(
                deposit,
                DepositTransaction.TransactionType.WITHDRAWAL,
                amount,
                "Снятие процентов",
                deposit.getCurrentBalance()
        );
        transactionRepository.save(transaction);
    }
    
    // Дополнительные методы
    
    public void addToDeposit(Long depositId, BigDecimal amount) {
        ClientDeposit deposit = clientDepositRepository.findById(depositId)
                .orElseThrow(() -> new RuntimeException("Депозит не найден"));
        
        deposit.setCurrentBalance(deposit.getCurrentBalance().add(amount));
        clientDepositRepository.save(deposit);
        
        DepositTransaction transaction = new DepositTransaction(
                deposit,
                DepositTransaction.TransactionType.DEPOSIT,
                amount,
                "Пополнение депозита",
                deposit.getCurrentBalance()
        );
        transactionRepository.save(transaction);
    }
    
    public List<DepositProduct> getAllActiveProducts() {
        return depositProductRepository.findByActiveTrue();
    }
    
    public List<ClientDeposit> getClientDeposits(Long clientId) {
        return clientDepositRepository.findByClientId(clientId);
    }
    
    public List<DepositTransaction> getDepositTransactions(Long depositId) {
        ClientDeposit deposit = clientDepositRepository.findById(depositId)
                .orElseThrow(() -> new RuntimeException("Депозит не найден"));
        return transactionRepository.findByClientDepositOrderByTransactionDateDesc(deposit);
    }
    
    public List<DepositTransaction> getTransactionsForPeriod(Long depositId, LocalDateTime startDate, LocalDateTime endDate) {
        ClientDeposit deposit = clientDepositRepository.findById(depositId)
                .orElseThrow(() -> new RuntimeException("Депозит не найден"));
        return transactionRepository.findByClientDepositAndDateRange(deposit, startDate, endDate);
    }
    
    public Optional<ClientDeposit> findDepositById(Long id) {
        return clientDepositRepository.findById(id);
    }
}
