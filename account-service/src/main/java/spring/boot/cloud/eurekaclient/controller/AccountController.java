package spring.boot.cloud.eurekaclient.controller;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.boot.cloud.eurekaclient.exception.ResourceNotFoundException;
import spring.boot.cloud.eurekaclient.model.Account;
import spring.boot.cloud.eurekaclient.model.external.Transaction;
import spring.boot.cloud.eurekaclient.repository.AccountRepository;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
public class AccountController {

    @Autowired
    AccountRepository accountRepository;

    // Get All Accounts
    @GetMapping("/accounts")
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    // Create a new Account
    @PostMapping("/accounts")
    public Account createAccount(@Valid @RequestBody Account account) {
        return accountRepository.save(account);
    }

    // Get a Single Account
    @GetMapping("/accounts/{id}")
    public Account getAccountById(@PathVariable(value = "id") Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", accountId));
    }

    // Update an Account
    @PutMapping("/accounts/{id}")
    public Account updateAccount(@PathVariable(value = "id") Long accountId,
                                 @Valid @RequestBody Account accountDetails) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", accountId));

        account.setAccountNumber(accountDetails.getAccountNumber());
        account.setBalance(accountDetails.getBalance());
        account.setCustomerId(accountDetails.getCustomerId());

        Account updatedAccount = accountRepository.save(account);
        return updatedAccount;
    }

    // Update an Account's Amount
    @PutMapping("/accounts")
    public Account updateAccountAmount(@Valid @RequestBody Transaction transaction) {

        Account account = accountRepository.findById(transaction.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", transaction.getAccountId()));

        account.setBalance(account.getBalance() + transaction.getAmount());

        Account updatedAccount = accountRepository.save(account);
        return updatedAccount;
    }


    // Delete an Account
    @DeleteMapping("/accounts/{id}")
    public ResponseEntity<?> deleteAccount(@PathVariable(value = "id") Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", accountId));

        accountRepository.delete(account);

        return ResponseEntity.ok().build();
    }

}
