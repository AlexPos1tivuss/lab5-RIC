
package com.bank.controller;

import com.bank.entity.*;
import com.bank.service.ClientService;
import com.bank.service.DepositService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class MainController {
    
    @Autowired
    private ClientService clientService;
    
    @Autowired
    private DepositService depositService;
    
    @GetMapping("/")
    public String index() {
        return "index";
    }
    
    @GetMapping("/clients/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("client", new Client());
        return "clients/register";
    }
    
    @PostMapping("/clients/register")
    public String registerClient(@Valid @ModelAttribute Client client, 
                                BindingResult result, 
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "clients/register";
        }
        
        try {
            clientService.registerClient(client);
            redirectAttributes.addFlashAttribute("message", "Клиент успешно зарегистрирован!");
            return "redirect:/clients/" + client.getId();
        } catch (Exception e) {
            result.rejectValue("email", "error.client", e.getMessage());
            return "clients/register";
        }
    }
    
    @GetMapping("/clients/{id}")
    public String clientDashboard(@PathVariable Long id, Model model) {
        Client client = clientService.findById(id)
                .orElseThrow(() -> new RuntimeException("Клиент не найден"));
        
        List<ClientDeposit> deposits = depositService.getClientDeposits(id);
        
        model.addAttribute("client", client);
        model.addAttribute("deposits", deposits);
        return "clients/dashboard";
    }
    
    @GetMapping("/deposits/products")
    public String showDepositProducts(Model model) {
        model.addAttribute("products", depositService.getAllActiveProducts());
        return "deposits/products";
    }
    
    @GetMapping("/deposits/open")
    public String showOpenDepositForm(@RequestParam Long clientId, 
                                     @RequestParam Long productId, 
                                     Model model) {
        Client client = clientService.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Клиент не найден"));
        
        List<DepositProduct> products = depositService.getAllActiveProducts();
        
        model.addAttribute("client", client);
        model.addAttribute("products", products);
        model.addAttribute("selectedProductId", productId);
        return "deposits/open";
    }
    
    @PostMapping("/deposits/open")
    public String openDeposit(@RequestParam Long clientId,
                             @RequestParam Long productId,
                             @RequestParam BigDecimal amount,
                             @RequestParam Integer termMonths,
                             RedirectAttributes redirectAttributes) {
        try {
            ClientDeposit deposit = depositService.openDeposit(clientId, productId, amount, termMonths);
            redirectAttributes.addFlashAttribute("message", "Депозит успешно открыт!");
            return "redirect:/deposits/" + deposit.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/clients/" + clientId;
        }
    }
    
    @GetMapping("/deposits/{id}")
    public String depositDetails(@PathVariable Long id, Model model) {
        ClientDeposit deposit = depositService.findDepositById(id)
                .orElseThrow(() -> new RuntimeException("Депозит не найден"));
        
        BigDecimal currentBalance = depositService.calculateBalance(id, LocalDateTime.now());
        List<DepositTransaction> transactions = depositService.getDepositTransactions(id);
        
        model.addAttribute("deposit", deposit);
        model.addAttribute("currentBalance", currentBalance);
        model.addAttribute("transactions", transactions);
        return "deposits/details";
    }
    
    @PostMapping("/deposits/{id}/calculate-balance")
    public String calculateBalance(@PathVariable Long id,
                                  @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date,
                                  RedirectAttributes redirectAttributes) {
        try {
            BigDecimal balance = depositService.calculateBalance(id, date);
            redirectAttributes.addFlashAttribute("calculatedBalance", balance);
            redirectAttributes.addFlashAttribute("calculationDate", date);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/deposits/" + id;
    }
    
    @PostMapping("/deposits/{id}/calculate-interest")
    public String calculateInterest(@PathVariable Long id,
                                   @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                   @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
                                   RedirectAttributes redirectAttributes) {
        try {
            BigDecimal interest = depositService.calculateInterestForPeriod(id, startDate, endDate);
            redirectAttributes.addFlashAttribute("calculatedInterest", interest);
            redirectAttributes.addFlashAttribute("interestPeriod", startDate + " - " + endDate);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/deposits/" + id;
    }
    
    @PostMapping("/deposits/{id}/add-funds")
    public String addFunds(@PathVariable Long id,
                          @RequestParam BigDecimal amount,
                          RedirectAttributes redirectAttributes) {
        try {
            depositService.addToDeposit(id, amount);
            redirectAttributes.addFlashAttribute("message", "Депозит успешно пополнен!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/deposits/" + id;
    }
    
    @PostMapping("/deposits/{id}/withdraw-interest")
    public String withdrawInterest(@PathVariable Long id,
                                  @RequestParam BigDecimal amount,
                                  RedirectAttributes redirectAttributes) {
        try {
            depositService.withdrawInterest(id, amount);
            redirectAttributes.addFlashAttribute("message", "Проценты успешно сняты!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/deposits/" + id;
    }
    
    @GetMapping("/deposits/{id}/statement")
    public String depositStatement(@PathVariable Long id,
                                  @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                  @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
                                  Model model) {
        ClientDeposit deposit = depositService.findDepositById(id)
                .orElseThrow(() -> new RuntimeException("Депозит не найден"));
        
        List<DepositTransaction> transactions;
        if (startDate != null && endDate != null) {
            transactions = depositService.getTransactionsForPeriod(id, startDate, endDate);
        } else {
            transactions = depositService.getDepositTransactions(id);
        }
        
        model.addAttribute("deposit", deposit);
        model.addAttribute("transactions", transactions);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        return "deposits/statement";
    }
}
