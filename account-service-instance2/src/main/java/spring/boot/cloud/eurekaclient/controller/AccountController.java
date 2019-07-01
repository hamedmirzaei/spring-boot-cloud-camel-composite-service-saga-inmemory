package spring.boot.cloud.eurekaclient.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.boot.cloud.eurekaclient.model.Account;
import spring.boot.cloud.eurekaclient.model.external.Transaction;
import spring.boot.cloud.eurekaclient.service.AccountService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
public class AccountController {

    @Autowired
    AccountService accountService;

    @Value("${spring.application.name}")
    private String appName;

    @Value("${server.port}")
    private String portNumber;

    // Health Check
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Health Check From " + appName + ":"  + portNumber);
    }

    // Get All Accounts
    @GetMapping("/accounts")
    public List<Account> getAllAccounts() {
        return accountService.findAll();
    }

    // Create a new Account
    @PostMapping("/accounts")
    public Account createAccount(@Valid @RequestBody Account account) {
        return accountService.save(account);
    }

    // Get a Single Account
    @GetMapping("/accounts/{id}")
    public Account getAccountById(@PathVariable(value = "id") Long accountId) {
        return accountService.findById(accountId);
    }

    // Update an Account
    @PutMapping("/accounts/{id}")
    public Account updateAccount(@PathVariable(value = "id") Long accountId,
                                 @Valid @RequestBody Account accountDetails) {
        return accountService.updateAccount(accountId, accountDetails);
    }

    // Update an Account's Amount
    @PutMapping("/accounts")
    public Account updateAccountAmount(@Valid @RequestBody Transaction transaction) {
        return accountService.updateAccountAmount(transaction);
    }

    // Update an Account's Status
    @PutMapping("/accounts/complete")
    public Account updateAccountStatus(@Valid @RequestBody Transaction transaction) {
        return accountService.completeUpdateAccountAmount(transaction);
    }

    // Rollback Update an Account's Amount
    @PutMapping("/accounts/rollback")
    public Account rollbackUpdateAccountAmount(@Valid @RequestBody Transaction transaction) {
        return accountService.rollbackUpdateAccountAmount(transaction);
    }

    // Delete an Account
    @DeleteMapping("/accounts/{id}")
    public ResponseEntity<?> deleteAccount(@PathVariable(value = "id") Long accountId) {
        accountService.delete(accountId);
        return ResponseEntity.ok().build();
    }

}
