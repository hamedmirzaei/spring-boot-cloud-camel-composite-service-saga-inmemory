package spring.boot.cloud.eurekaclient.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.boot.cloud.eurekaclient.exception.ResourceNotFoundException;
import spring.boot.cloud.eurekaclient.model.Account;
import spring.boot.cloud.eurekaclient.model.AccountStatus;
import spring.boot.cloud.eurekaclient.model.external.Transaction;
import spring.boot.cloud.eurekaclient.repository.AccountRepository;

import java.util.List;

@Service
public class AccountService {

    @Autowired
    AccountRepository accountRepository;

    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    public Account save(Account account) {
        return accountRepository.save(account);
    }

    public Account findById(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", accountId));
    }

    @Transactional
    public Account updateAccount(Long accountId, Account accountDetails) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", accountId));

        account.setAccountNumber(accountDetails.getAccountNumber());
        account.setBalance(accountDetails.getBalance());
        account.setCustomerId(accountDetails.getCustomerId());

        Account updatedAccount = accountRepository.save(account);
        return updatedAccount;
    }

    @Transactional
    public Account updateAccountAmount(Transaction transaction) {
        Account account = accountRepository.findById(transaction.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", transaction.getAccountId()));

        account.setBalance(account.getBalance() + transaction.getAmount());
        account.setStatus(AccountStatus.CHANGED);

        Account updatedAccount = accountRepository.save(account);
        return updatedAccount;
    }

    @Transactional
    public Account rollbackUpdateAccountAmount(Transaction transaction) {
        Account account = accountRepository.findById(transaction.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", transaction.getAccountId()));

        if (account.getStatus().equals(AccountStatus.IDEAL))
            return account;

        account.setBalance(account.getBalance() - transaction.getAmount());
        account.setStatus(AccountStatus.IDEAL);

        Account updatedAccount = accountRepository.save(account);
        return updatedAccount;
    }

    @Transactional
    public void delete(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", accountId));

        accountRepository.delete(account);
    }

}
