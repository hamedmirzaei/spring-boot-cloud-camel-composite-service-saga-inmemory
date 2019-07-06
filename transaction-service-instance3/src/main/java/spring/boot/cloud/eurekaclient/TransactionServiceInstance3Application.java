package spring.boot.cloud.eurekaclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@EnableEurekaClient
@EnableDiscoveryClient
public class TransactionServiceInstance3Application {

    public static void main(String[] args) {
        SpringApplication.run(TransactionServiceInstance3Application.class, args);
    }

}
