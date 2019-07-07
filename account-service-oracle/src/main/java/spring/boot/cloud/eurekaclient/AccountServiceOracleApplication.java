package spring.boot.cloud.eurekaclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.h2.tools.Server;

import java.sql.SQLException;

@SpringBootApplication
@EnableJpaAuditing
@EnableEurekaClient
@EnableDiscoveryClient
public class AccountServiceOracleApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccountServiceOracleApplication.class, args);
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public Server inMemoryH2DatabaseServer() throws SQLException {
        return Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9091");
    }

}
