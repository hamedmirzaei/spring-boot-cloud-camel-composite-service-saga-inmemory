package spring.boot.cloud.eurekaclient;

import lombok.extern.slf4j.Slf4j;
import org.fluttercode.datafactory.impl.DataFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import spring.boot.cloud.eurekaclient.model.Account;
import spring.boot.cloud.eurekaclient.model.AccountStatus;
import spring.boot.cloud.eurekaclient.model.Customer;
import spring.boot.cloud.eurekaclient.service.AccountService;
import spring.boot.cloud.eurekaclient.service.CustomerService;

@Component
@ConditionalOnProperty(name = "app.db-init", havingValue = "true")
@Slf4j
public class DbInitializer implements CommandLineRunner {

    @Autowired
    AccountService accountService;

    @Autowired
    CustomerService customerService;

    @Override
    public void run(String... args) throws Exception {

        DataFactory df = new DataFactory();
        for (Long i = 1L; i <= 40L; i++) {
            Customer customer = new Customer();
            customer.setId(i);
            customer.setFirstName(df.getFirstName());
            customer.setLastName(df.getLastName());
            customer.setCifNumber(Long.parseLong(df.getNumberText(10)));
            customerService.save(customer);
        }

        for (Long i = 1L; i <= 2000L; i++) {
            Account account = new Account();
            account.setId(i);
            account.setAccountNumber(df.getNumberText(13));
            account.setCustomerId(Long.parseLong(df.getNumberBetween(1, 41) + ""));
            account.setBalance(Long.parseLong(df.getNumberBetween(1000000, 4000000) + ""));
            account.setStatus(AccountStatus.IDEAL);
            account.setLastTransactionId(0L);
            accountService.save(account);
        }
    }
}
