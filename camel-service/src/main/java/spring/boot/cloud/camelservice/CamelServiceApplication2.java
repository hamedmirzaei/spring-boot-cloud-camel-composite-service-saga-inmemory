/*
package spring.boot.cloud.camelservice;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.http.common.HttpOperationFailedException;
import org.apache.camel.impl.saga.InMemorySagaService;
import org.apache.camel.model.SagaPropagation;
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
public class CamelServiceApplication2 extends SpringRouteBuilder {

    private final String ACCOUNT_EUREKA_SERVICE_NAME = "account-service";
    private final String CUSTOMER_EUREKA_SERVICE_NAME = "account-service";
    private final String TRANSACTION_EUREKA_SERVICE_NAME = "transaction-service";

    public static void main(String[] args) {
        SpringApplication.run(CamelServiceApplication2.class, args);
    }

    @Autowired
    CamelContext camelContext;

    @Autowired
    GroupedExchangeAggregationStrategy groupedExchangeAggregationStrategy;

    @Autowired
    JsonToObjectConverter jsonToObjectConverter;

    @Override
    public void configure() throws Exception {

        camelContext.addService(new InMemorySagaService());
        restConfiguration().port(8764).host("localhost");

        rest().description("Camel rest service")
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
                .log("transaction-service : ${body}");



        Class[] exceptionClasses = {HttpOperationFailedException.class, IllegalArgumentException.class};
        rest().description("Camel rest service")
                .consumes("application/json").produces("application/json")
                .post("/make-transaction")
                .description("make a transaction")
                .outType(String.class)
                .route()
                .removeHeaders("CamelHttp*")
                .saga()
                    .timeout(2, TimeUnit.MINUTES)
                    .multicast()
                        .parallelProcessing()
                            .to("direct:update-account")
                            .to("direct:add-transaction");

        from("direct:update-account")
                .log("Start of direct:update-account with body: ${body}")
                .removeHeader("CamelHttp*")
                .removeHeader(Exchange.HTTP_PATH)
                .removeHeader(Exchange.HTTP_URI)
                .setHeader(Exchange.HTTP_METHOD, constant("PUT"))

                .saga()
                .option("tnx", simple("${body}"))
                .timeout(2, TimeUnit.MINUTES)
                .propagation(SagaPropagation.MANDATORY)
                .compensation("direct:update-account-compensation")
                .doTry()
                    .serviceCall(ACCOUNT_EUREKA_SERVICE_NAME + "/api/accounts")
                    .convertBodyTo(String.class)
                    .process(new Processor() {
                        @Override
                        public void process(Exchange exchange) throws Exception {
                            throw new IllegalArgumentException("################### " + exchange.getIn().getBody());
                        }
                    })
                    .log("End of direct:update-account with body: ${body}")
                .doCatch(exceptionClasses)
                    .throwException(new RuntimeException("direct:update-account failed ${body}"))
                .endDoTry();

        from("direct:update-account-compensation")
                .setBody(header("tnx")).convertBodyTo(String.class)
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        exchange.getIn().getBody();
                    }
                })
                .log("Start of direct:update-account-compensation with body: ${body}");


        from("direct:add-transaction")
                .log("Start of direct:add-transaction with body: ${body}")
                .removeHeader("CamelHttp*")
                .removeHeader(Exchange.HTTP_PATH)
                .removeHeader(Exchange.HTTP_URI)
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .serviceCall(TRANSACTION_EUREKA_SERVICE_NAME + "/api/transactions")
                .convertBodyTo(String.class)
                .log("End of direct:add-transaction with body: ${body}");



        */
/*.param().type(RestParamType.header).name("accountId").required(true).endParam()
                .param().type(RestParamType.header).name("amount").required(true).endParam()
                .route()
                .saga()
                .timeout(2, TimeUnit.MINUTES)
                    .propagation(SagaPropagation.MANDATORY)
                    .option("accountId", header("accountId"))
                    .option("amount", header("amount"))
                    .compensation("direct:cancelAccountTransaction")
                    .transform().header(Exchange.SAGA_LONG_RUNNING_ACTION)
                .doTry()
                    .serviceCall(ACCOUNT_EUREKA_SERVICE_NAME + "/api/accounts/${header.accountId}/${header.amount}")
                .doCatch(exceptionClasses)
                    .throwException(new RuntimeException("transaction for account #${header.accountId} failed"))
                .endDoTry();

        from("direct:cancelAccountTransaction")
                .serviceCall(ACCOUNT_EUREKA_SERVICE_NAME + "/api/accounts/${header.accountId}/-${header.amount}");*//*

    }

    @Bean
    public GroupedExchangeAggregationStrategy groupedExchangeAggregationStrategy() {
        return new GroupedExchangeAggregationStrategy();
    }

    @Bean
    public JsonToObjectConverter jsonToObjectConverter() {
        return new JsonToObjectConverter();
    }
}
*/
