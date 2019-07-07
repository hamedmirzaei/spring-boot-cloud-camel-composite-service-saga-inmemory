package spring.boot.cloud.eurekaclient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import spring.boot.cloud.eurekaclient.service.TransactionService;

@Component
@ConditionalOnProperty(name = "app.db-init", havingValue = "true")
@Slf4j
public class DbInitializer implements CommandLineRunner {

    @Autowired
    TransactionService transactionService;

    @Override
    public void run(String... args) throws Exception {
        // nothing here
    }
}
