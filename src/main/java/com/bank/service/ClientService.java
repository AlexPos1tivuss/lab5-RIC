
package com.bank.service;

import com.bank.entity.Client;
import com.bank.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ClientService {
    
    @Autowired
    private ClientRepository clientRepository;
    
    public Client registerClient(Client client) {
        if (clientRepository.existsByEmail(client.getEmail())) {
            throw new RuntimeException("Клиент с таким email уже существует");
        }
        if (clientRepository.existsByPhone(client.getPhone())) {
            throw new RuntimeException("Клиент с таким телефоном уже существует");
        }
        
        return clientRepository.save(client);
    }
    
    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }
    
    public Optional<Client> findById(Long id) {
        return clientRepository.findById(id);
    }
    
    public Optional<Client> findByEmail(String email) {
        return clientRepository.findByEmail(email);
    }
    
    public Client updateClient(Client client) {
        return clientRepository.save(client);
    }
}
