package spring.boot.cloud.eurekaclient.controller;

import org.springframework.beans.factory.annotation.Autowired;
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

    // Update a Transaction
    @PutMapping("/transactions/{id}")
    public Transaction updateTransaction(@PathVariable(value = "id") Long transactionId,
                                         @Valid @RequestBody Transaction transactionDetails) {
        return transactionService.updateTransaction(transactionId, transactionDetails);
    }


    // Delete a Transaction
    @DeleteMapping("/transactions/{id}")
    public ResponseEntity<?> deleteTransaction(@PathVariable(value = "id") Long transactionId) {
        transactionService.delete(transactionId);
        return ResponseEntity.ok().build();
    }

}
