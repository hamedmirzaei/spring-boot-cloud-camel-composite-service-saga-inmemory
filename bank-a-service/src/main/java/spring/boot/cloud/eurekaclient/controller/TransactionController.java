package spring.boot.cloud.eurekaclient.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.boot.cloud.eurekaclient.model.Transaction;
import spring.boot.cloud.eurekaclient.service.TransactionService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
public class TransactionController {

    @Autowired
    TransactionService transactionService;

    @Value("${spring.application.name}")
    private String appName;

    @Value("${server.port}")
    private String portNumber;

    // Health Check
    @GetMapping("/transactions/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Health Check From " + appName + ":"  + portNumber);
    }

    // Get All Transactions
    @GetMapping("/transactions")
    public List<Transaction> getAllTransactions() {
        return transactionService.findAll();
    }

    // Create a new Transaction
    @PostMapping("/transactions")
    public Transaction createTransaction(@Valid @RequestBody Transaction transaction) {
        return transactionService.save(transaction);
    }

    // Get a Single Transaction
    @GetMapping("/transactions/{id}")
    public Transaction getTransactionById(@PathVariable(value = "id") Long transactionId) {
        return transactionService.findById(transactionId);
    }

    // Delete a Transaction
    @DeleteMapping("/transactions/{id}")
    public ResponseEntity<?> deleteTransaction(@PathVariable(value = "id") Long transactionId) {
        transactionService.delete(transactionId);
        return ResponseEntity.ok().build();
    }

}
