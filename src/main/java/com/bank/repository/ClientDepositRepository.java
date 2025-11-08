
package com.bank.repository;

import com.bank.entity.ClientDeposit;
import com.bank.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientDepositRepository extends JpaRepository<ClientDeposit, Long> {
    List<ClientDeposit> findByClient(Client client);
    List<ClientDeposit> findByClientId(Long clientId);
}
