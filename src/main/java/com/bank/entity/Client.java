
package com.bank.entity;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.List;

@Entity
@Table(name = "clients")
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Имя не может быть пустым")
    @Column(nullable = false)
    private String firstName;
    
    @NotBlank(message = "Фамилия не может быть пустой")
    @Column(nullable = false)
    private String lastName;
    
    @Email(message = "Некорректный email")
    @Column(unique = true)
    private String email;
    
    @Pattern(regexp = "\\+7\\d{10}", message = "Телефон должен быть в формате +7XXXXXXXXXX")
    @Column(unique = true)
    private String phone;
    
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ClientDeposit> deposits;
    
    // Constructors
    public Client() {}
    
    public Client(String firstName, String lastName, String email, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public List<ClientDeposit> getDeposits() { return deposits; }
    public void setDeposits(List<ClientDeposit> deposits) { this.deposits = deposits; }
    
    public String getFullName() {
        return firstName + " " + lastName;
    }
}
