package spring.boot.cloud.eurekaclient.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spring.boot.cloud.eurekaclient.model.Account;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    /*@Query ("update Account account set account.balance = account.balance + :amount, " +
            "account.status = 'CHANGED' where account.id = :accountId")
    public Account updateAccountBalanceByStatusAndAccountId(@Param("amount") Long amount,
                                                            @Param("accountId") Long accountId);*/

    public Optional<Account> findByIdAndStatus(Long id, String status);

}
