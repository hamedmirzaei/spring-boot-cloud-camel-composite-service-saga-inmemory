package spring.boot.cloud.camelservice;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.http.common.HttpMethods;
import org.apache.camel.impl.saga.InMemorySagaService;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.spring.SpringRouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import spring.boot.cloud.camelservice.model.external.Transaction;

import java.util.Random;
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

    @Override
    public void configure() throws Exception {

        camelContext.addService(new InMemorySagaService());
        camelContext.getExecutorServiceManager().getDefaultThreadPoolProfile().setMaxQueueSize(-1); // default id is defaultThreadPoolProfile
        restConfiguration().port(8764).host("localhost");

        rest("/health").description("Camel rest service")
                .get().description("health check")
                .route()
                .transform().constant("<html><head><title>Health Check</title></head><body>OK</body></html>");


        rest().description("Camel rest service")
                .consumes("application/json").produces("application/json")
                .post("/make-transaction").description("make a transaction")
                .outType(String.class).type(Transaction.class)
                .route()
                .removeHeaders("CamelHttp*")
                .saga()
                .option("tnx", simple("${body}"))
                .compensation("direct:cancel-transaction")
                .completion("direct:complete-transaction")
                .timeout(2, TimeUnit.MINUTES)
                .multicast()
                .parallelProcessing()
                .to("direct:update-account")
                .to("direct:add-transaction")
                .end();

        from("direct:complete-transaction")
                .setBody(header("tnx"))//.convertBodyTo(String.class)
                .log("Start of direct:complete-transaction with body: ${body}")
                .removeHeader("CamelHttp*")
                .removeHeader(Exchange.HTTP_PATH)
                .removeHeader(Exchange.HTTP_URI)
                //.multicast().parallelProcessing()
                .to("direct:complete-update-account")
                .log("End of direct:complete-transaction with body: ${body}");

        from("direct:cancel-transaction")
                .log("Start of direct:cancel-transaction with body: ${body}")
                .removeHeader("CamelHttp*")
                .removeHeader(Exchange.HTTP_PATH)
                .removeHeader(Exchange.HTTP_URI)
                .to("direct:rollback-update-account")
                .to("direct:rollback-add-transaction")
                .log("End of direct:cancel-transaction with body: ${body}");


        from("direct:update-account")
                .log("Start of direct:update-account with body: ${body}")
                .removeHeader("CamelHttp*")
                .removeHeader(Exchange.HTTP_PATH)
                .removeHeader(Exchange.HTTP_URI)
                .setHeader(Exchange.HTTP_METHOD, constant(HttpMethods.PUT))
                .serviceCall(ACCOUNT_EUREKA_SERVICE_NAME + "/api/accounts")
                .convertBodyTo(String.class)
                .log("End of direct:update-account with body: ${body}");

        from("direct:complete-update-account")
                .setBody(header("tnx")).convertBodyTo(String.class)
                .unmarshal().json(JsonLibrary.Jackson, Transaction.class)
                .marshal().json(JsonLibrary.Jackson)
                .log("Start of direct:complete-update-account with body: ${body}")
                .removeHeader("CamelHttp*")
                .removeHeader(Exchange.HTTP_PATH)
                .removeHeader(Exchange.HTTP_URI)
                .setHeader(Exchange.HTTP_METHOD, constant(HttpMethods.PUT))
                .serviceCall(ACCOUNT_EUREKA_SERVICE_NAME + "/api/accounts/complete")
                .convertBodyTo(String.class)
                .log("End of direct:complete-update-account with body: ${body}");

        from("direct:rollback-update-account")
                .setBody(header("tnx")).convertBodyTo(String.class)
                .unmarshal().json(JsonLibrary.Jackson, Transaction.class)
                .marshal().json(JsonLibrary.Jackson)
                .log("Start of direct:rollback-update-account with body: ${body}")
                .removeHeader("CamelHttp*")
                .removeHeader(Exchange.HTTP_PATH)
                .removeHeader(Exchange.HTTP_URI)
                .setHeader(Exchange.HTTP_METHOD, constant(HttpMethods.PUT))
                .serviceCall(ACCOUNT_EUREKA_SERVICE_NAME + "/api/accounts/rollback")
                .convertBodyTo(String.class)
                .log("End of direct:rollback-update-account with body: ${body}");


        from("direct:add-transaction")
                .log("Start of direct:add-transaction with body: ${body}")
                .removeHeader("CamelHttp*")
                .removeHeader(Exchange.HTTP_PATH)
                .removeHeader(Exchange.HTTP_URI)
                .setHeader(Exchange.HTTP_METHOD, constant(HttpMethods.POST))
                .serviceCall(TRANSACTION_EUREKA_SERVICE_NAME + "/api/transactions")
                .convertBodyTo(String.class)
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        exchange.getIn().setHeader("random-x", new Random(System.currentTimeMillis()).nextInt(100));
                    }
                })
                .log("################################ random-x is: ${header[random-x]}")
                .choice()
                    .when(header("random-x").isGreaterThan(100))
                        .throwException(new RuntimeException("Random-x failure during direct:add-transaction"))
                .end()
                .log("End of direct:add-transaction with body: ${body}");

        from("direct:rollback-add-transaction")
                .setBody(header("tnx")).convertBodyTo(String.class)
                .unmarshal().json(JsonLibrary.Jackson, Transaction.class)
                .log("Start of direct:rollback-add-transaction with body: ${body}")
                .removeHeader("CamelHttp*")
                .removeHeader(Exchange.HTTP_PATH)
                .removeHeader(Exchange.HTTP_URI)
                .setHeader(Exchange.HTTP_METHOD, constant(HttpMethods.DELETE))
                .setHeader("id", simple("${body.id}"))
                .setBody(simple(""))
                .serviceCall(TRANSACTION_EUREKA_SERVICE_NAME + "/api/transactions/${header[id]}")
                .convertBodyTo(String.class)
                .log("End of direct:rollback-add-transaction with body: ${body}");

    }
}
