package spring.boot.cloud.eurekaclient.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.boot.cloud.eurekaclient.exception.ResourceNotFoundException;
import spring.boot.cloud.eurekaclient.model.Transaction;
import spring.boot.cloud.eurekaclient.repository.TransactionRepository;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
public class TransactionController {

    @Autowired
    TransactionRepository transactionRepository;

    // Get All Transactions
    @GetMapping("/transactions")
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    // Create a new Transaction
    @PostMapping("/transactions")
    public Transaction createTransaction(@Valid @RequestBody Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    // Get a Single Transaction
    @GetMapping("/transactions/{id}")
    public Transaction getTransactionById(@PathVariable(value = "id") Long transactionId) {
        return transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", "id", transactionId));
    }

    // Update a Transaction
    @PutMapping("/transactions/{id}")
    public Transaction updateTransaction(@PathVariable(value = "id") Long transactionId,
                                      @Valid @RequestBody Transaction transactionDetails) {

        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", "id", transactionId));

        transaction.setAccountId(transactionDetails.getAccountId());
        transaction.setAmount(transactionDetails.getAmount());

        Transaction updatedTransaction = transactionRepository.save(transaction);
        return updatedTransaction;
    }


    // Delete a Transaction
    @DeleteMapping("/transactions/{id}")
    public ResponseEntity<?> deleteTransaction(@PathVariable(value = "id") Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", "id", transactionId));

        transactionRepository.delete(transaction);

        return ResponseEntity.ok().build();
    }

}
