
package com.bank.repository;

import com.bank.entity.DepositTransaction;
import com.bank.entity.ClientDeposit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DepositTransactionRepository extends JpaRepository<DepositTransaction, Long> {
    List<DepositTransaction> findByClientDepositOrderByTransactionDateDesc(ClientDeposit clientDeposit);
    
    @Query("SELECT t FROM DepositTransaction t WHERE t.clientDeposit = :deposit " +
           "AND t.transactionDate BETWEEN :startDate AND :endDate " +
           "ORDER BY t.transactionDate DESC")
    List<DepositTransaction> findByClientDepositAndDateRange(
            @Param("deposit") ClientDeposit deposit,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}
