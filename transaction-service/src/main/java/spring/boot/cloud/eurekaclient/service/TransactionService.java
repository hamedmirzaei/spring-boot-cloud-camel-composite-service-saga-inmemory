package spring.boot.cloud.eurekaclient.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.boot.cloud.eurekaclient.exception.ResourceNotFoundException;
import spring.boot.cloud.eurekaclient.model.Transaction;
import spring.boot.cloud.eurekaclient.repository.TransactionRepository;

import java.util.List;

@Service
public class TransactionService {

    @Autowired
    TransactionRepository transactionRepository;

    public List<Transaction> findAll() {
        return transactionRepository.findAll();
    }

    public Transaction save(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    public Transaction findById(Long transactionId) {
        return transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", "id", transactionId));
    }

    @Transactional
    public Transaction updateTransaction(Long transactionId, Transaction transactionDetails) {

        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", "id", transactionId));

        transaction.setAccountId(transactionDetails.getAccountId());
        transaction.setAmount(transactionDetails.getAmount());

        Transaction updatedTransaction = transactionRepository.save(transaction);
        return updatedTransaction;
    }

    @Transactional
    public void delete(Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElse(null);
                //.orElseThrow(() -> new ResourceNotFoundException("Transaction", "id", transactionId));
        if (transaction != null)
            transactionRepository.delete(transaction);
    }

}
