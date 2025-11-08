
package com.bank.repository;

import com.bank.entity.DepositProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepositProductRepository extends JpaRepository<DepositProduct, Long> {
    List<DepositProduct> findByActiveTrue();
}
