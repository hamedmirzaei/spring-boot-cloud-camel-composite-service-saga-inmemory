package spring.boot.cloud.camelservice;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.impl.saga.InMemorySagaService;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.processor.aggregate.GroupedExchangeAggregationStrategy;
import org.apache.camel.spring.SpringRouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import spring.boot.cloud.camelservice.model.external.Transaction;
import spring.boot.cloud.camelservice.processor.JsonToObjectConverter;

import java.util.concurrent.TimeUnit;

@EnableEurekaClient
@EnableDiscoveryClient
@SpringBootApplication
@Slf4j
public class CamelServiceApplication extends SpringRouteBuilder {

    private final String ACCOUNT_EUREKA_SERVICE_NAME = "account-service";
    private final String CUSTOMER_EUREKA_SERVICE_NAME = "account-service";
    private final String TRANSACTION_EUREKA_SERVICE_NAME = "transaction-service";

    public static void main(String[] args) {
        SpringApplication.run(CamelServiceApplication.class, args);
    }

    @Autowired
    CamelContext camelContext;

    /*@Autowired
    GroupedExchangeAggregationStrategy groupedExchangeAggregationStrategy;

    @Autowired
    JsonToObjectConverter jsonToObjectConverter;*/

    @Override
    public void configure() throws Exception {

        camelContext.addService(new InMemorySagaService());
        restConfiguration().port(8764).host("localhost");


        /*rest().description("transacted multi-service call")
                .consumes("application/json").produces("application/json")
                .post("/make-transaction")
                .outType(String.class).type(Transaction.class)
                .route()
                .removeHeader("CamelHttp*")
                .transacted(PROPAGATION_REQUIRED_BEAN_NAME)
                .to("direct:add-transaction")
                .to("direct:update-account")
                .end();

        from("direct:update-account")
                .log("Start of direct:update-account with body: ${body}")
                .removeHeader("CamelHttp*")
                .removeHeader(Exchange.HTTP_PATH)
                .removeHeader(Exchange.HTTP_URI)
                .setHeader(Exchange.HTTP_METHOD, constant("PUT"))
                .serviceCall(ACCOUNT_EUREKA_SERVICE_NAME + "/api/accounts")
                .convertBodyTo(String.class)
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        throw new IllegalArgumentException("################### " + exchange.getIn().getBody());
                    }
                })
                .log("End of direct:update-account with body: ${body}");


        from("direct:add-transaction")
                .log("Start of direct:add-transaction with body: ${body}")
                .removeHeader("CamelHttp*")
                .removeHeader(Exchange.HTTP_PATH)
                .removeHeader(Exchange.HTTP_URI)
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .serviceCall(TRANSACTION_EUREKA_SERVICE_NAME + "/api/transactions")
                .convertBodyTo(String.class)
                .log("End of direct:add-transaction with body: ${body}");*/


        /*rest().description("Camel rest service")
                .consumes("application/json").produces("application/json")
                .get("/customers")
                .description("get customers").outType(String.class)
                .route()
                .enrich("direct:customers", groupedExchangeAggregationStrategy)
                .enrich("direct:transactions", groupedExchangeAggregationStrategy)
                .process(jsonToObjectConverter)
                .endRest();

        from("direct:customers")
                .removeHeader("CamelHttp*")
                .removeHeader(Exchange.HTTP_PATH)
                .removeHeader(Exchange.HTTP_URI)
                .serviceCall(ACCOUNT_EUREKA_SERVICE_NAME + "/api/customers")
                .convertBodyTo(String.class)
                .log("customer-service : ${body}");

        from("direct:transactions")
                .removeHeader("CamelHttp*")
                .removeHeader(Exchange.HTTP_PATH)
                .removeHeader(Exchange.HTTP_URI)
                .serviceCall(TRANSACTION_EUREKA_SERVICE_NAME + "/api/transactions")
                .convertBodyTo(String.class)
                .log("transaction-service : ${body}");*/


        rest().description("Camel rest service")
            .consumes("application/json").produces("application/json")
            .post("/make-transaction").description("make a transaction")
            .outType(String.class).type(Transaction.class)
            .route()
            .removeHeaders("CamelHttp*")
            .saga()
                .compensation("direct:cancel-transaction")
                .completion("")
                .option("tnx", simple("${body}"))
                .timeout(2, TimeUnit.MINUTES)
                .multicast()
                .parallelProcessing()
                    .to("direct:update-account")
                    .to("direct:add-transaction")
                .end()
            .end();


        from("direct:cancel-transaction")
            .log("Start of direct:cancel-transaction with body: ${body}")
            .removeHeader("CamelHttp*")
            .removeHeader(Exchange.HTTP_PATH)
            .removeHeader(Exchange.HTTP_URI)
            .multicast().parallelProcessing()
            .to("direct:rollback-update-account")
            .to("direct:rollback-add-transaction")
            .log("End of direct:update-account with body: ${body}");


        from("direct:update-account")
            .log("Start of direct:update-account with body: ${body}")
            .removeHeader("CamelHttp*")
            .removeHeader(Exchange.HTTP_PATH)
            .removeHeader(Exchange.HTTP_URI)
            .setHeader(Exchange.HTTP_METHOD, constant("PUT"))
            .serviceCall(ACCOUNT_EUREKA_SERVICE_NAME + "/api/accounts")
            .convertBodyTo(String.class)
            .log("End of direct:update-account with body: ${body}");

        from("direct:rollback-update-account")
            .log("Start of direct:rollback-update-account with body: ${body}")
            .removeHeader("CamelHttp*")
            .removeHeader(Exchange.HTTP_PATH)
            .removeHeader(Exchange.HTTP_URI)
            .setHeader(Exchange.HTTP_METHOD, constant("PUT"))
            .serviceCall(ACCOUNT_EUREKA_SERVICE_NAME + "/api/accounts/rollback")
            .convertBodyTo(String.class)
            .log("End of direct:rollback-update-account with body: ${body}");


        from("direct:add-transaction")
            .log("Start of direct:add-transaction with body: ${body}")
            .removeHeader("CamelHttp*")
            .removeHeader(Exchange.HTTP_PATH)
            .removeHeader(Exchange.HTTP_URI)
            .setHeader(Exchange.HTTP_METHOD, constant("POST"))
            .serviceCall(TRANSACTION_EUREKA_SERVICE_NAME + "/api/transactions")
            .convertBodyTo(String.class)
            .log("End of direct:add-transaction with body: ${body}");

        from("direct:rollback-add-transaction")
            .log("Start of direct:rollback-add-transaction with body: ${body}")
            .setBody(header("order")).convertBodyTo(String.class)
            .unmarshal().json(JsonLibrary.Jackson, Transaction.class)
            .removeHeader("CamelHttp*")
            .removeHeader(Exchange.HTTP_PATH)
            .removeHeader(Exchange.HTTP_URI)
            .setHeader(Exchange.HTTP_METHOD, constant("DELETE"))
            .serviceCall(ACCOUNT_EUREKA_SERVICE_NAME + "/api/transactions/${body.id}")
            .convertBodyTo(String.class)
            .log("End of direct:rollback-add-transaction with body: ${body}");

    }

    /*@Bean
    public GroupedExchangeAggregationStrategy groupedExchangeAggregationStrategy() {
        return new GroupedExchangeAggregationStrategy();
    }

    @Bean
    public JsonToObjectConverter jsonToObjectConverter() {
        return new JsonToObjectConverter();
    }*/

    /*@Bean ("transactionManager")
    public PlatformTransactionManager platformTransactionManager(DataSource dataSource) {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
        transactionManager.setDataSource(dataSource);
        return transactionManager;
    }

    private final String PROPAGATION_MANDATORY_BEAN_NAME = "PROPAGATION_MANDATORY_BEAN";
    private final String PROPAGATION_REQUIRED_BEAN_NAME = "PROPAGATION_REQUIRED_BEAN";

    @Bean(PROPAGATION_MANDATORY_BEAN_NAME)
    public SpringTransactionPolicy propagationMandatoryPolicy(PlatformTransactionManager transactionManager) {
        SpringTransactionPolicy springTransactionPolicy = new SpringTransactionPolicy();
        springTransactionPolicy.setTransactionManager(transactionManager);
        springTransactionPolicy.setPropagationBehaviorName("PROPAGATION_MANDATORY");
        return springTransactionPolicy;
    }

    @Bean(PROPAGATION_REQUIRED_BEAN_NAME)
    public SpringTransactionPolicy propagationRequiredPolicy(PlatformTransactionManager transactionManager) {
        SpringTransactionPolicy springTransactionPolicy = new SpringTransactionPolicy();
        springTransactionPolicy.setTransactionManager(transactionManager);
        springTransactionPolicy.setPropagationBehaviorName("PROPAGATION_REQUIRED");
        return springTransactionPolicy;
    }*/
}
