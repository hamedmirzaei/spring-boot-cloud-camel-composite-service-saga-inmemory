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
public class CamelTransferServiceApplication extends SpringRouteBuilder {

    private final String BANK_A_EUREKA_SERVICE_NAME = "bank-a-service";
    private final String BANK_B_EUREKA_SERVICE_NAME = "bank-b-service";

    public static void main(String[] args) {
        SpringApplication.run(CamelTransferServiceApplication.class, args);
    }

    @Autowired
    CamelContext camelContext;

    @Override
    public void configure() throws Exception {

        camelContext.addService(new InMemorySagaService());
        camelContext.getExecutorServiceManager().getDefaultThreadPoolProfile().setMaxQueueSize(-1); // default id is defaultThreadPoolProfile
        restConfiguration().port(8767).host("localhost");

        rest("/health").description("Camel transfer service")
                .get().description("health check")
                .route()
                .transform().constant("<html><head><title>Health Check</title></head><body>OK</body></html>");


        rest().description("Camel transfer service")
                .consumes("application/json").produces("application/json")
                .post("/transfer").description("Transfer From Bank A to Bank B")
                .outType(String.class).type(Transaction.class)
                .route()
                .removeHeaders("CamelHttp*")
                .saga()
                .option("tnx", simple("${body}"))
                .completion("direct:complete-transfer")
                .compensation("direct:cancel-transfer")
                .timeout(2, TimeUnit.MINUTES)
                .multicast()
                .parallelProcessing()
                .to("direct:transfer-bank-a")
                .to("direct:transfer-bank-b")
                .end();

        from("direct:complete-transfer")
                .setBody(header("tnx"))//.convertBodyTo(String.class)
                .log("Start of direct:complete-transfer with body: ${body}")
                .removeHeader("CamelHttp*")
                .removeHeader(Exchange.HTTP_PATH)
                .removeHeader(Exchange.HTTP_URI)
                .log("End of direct:complete-transfer with body: ${body}");

        from("direct:cancel-transfer")
                .log("Start of direct:cancel-transfer with body: ${body}")
                .removeHeader("CamelHttp*")
                .removeHeader(Exchange.HTTP_PATH)
                .removeHeader(Exchange.HTTP_URI)
                .multicast().parallelProcessing()
                .to("direct:rollback-transfer-bank-a")
                .to("direct:rollback-transfer-bank-b")
                .log("End of direct:cancel-transfer with body: ${body}");


        from("direct:transfer-bank-a")
                .log("Start of direct:transfer-bank-a with body: ${body}")
                .removeHeader("CamelHttp*")
                .removeHeader(Exchange.HTTP_PATH)
                .removeHeader(Exchange.HTTP_URI)
                .setHeader(Exchange.HTTP_METHOD, constant(HttpMethods.POST))
                .serviceCall(BANK_A_EUREKA_SERVICE_NAME + "/api/transactions")
                .convertBodyTo(String.class)
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        exchange.getIn().setHeader("random-x", new Random(System.currentTimeMillis()).nextInt(100));
                    }
                })
                .log("################################ random-x is: ${header[random-x]}")
                .choice()
                    .when(header("random-x").isGreaterThan(90))
                        .throwException(new RuntimeException("Random failure during direct:transfer-bank-a"))
                .end()
                .log("End of direct:transfer-bank-a with body: ${body}");

        from("direct:rollback-transfer-bank-a")
                .setBody(header("tnx")).convertBodyTo(String.class)
                .unmarshal().json(JsonLibrary.Jackson, Transaction.class)
                .log("Start of direct:rollback-transfer-bank-a with body: ${body}")
                .removeHeader("CamelHttp*")
                .removeHeader(Exchange.HTTP_PATH)
                .removeHeader(Exchange.HTTP_URI)
                .setHeader(Exchange.HTTP_METHOD, constant(HttpMethods.DELETE))
                .setHeader("id", simple("${body.id}"))
                .setBody(simple(""))
                .serviceCall(BANK_A_EUREKA_SERVICE_NAME + "/api/transactions/${header[id]}")
                .convertBodyTo(String.class)
                .log("End of direct:rollback-transfer-bank-a with body: ${body}");


        from("direct:transfer-bank-b")
                .log("Start of direct:transfer-bank-b with body: ${body}")
                .removeHeader("CamelHttp*")
                .removeHeader(Exchange.HTTP_PATH)
                .removeHeader(Exchange.HTTP_URI)
                .setHeader(Exchange.HTTP_METHOD, constant(HttpMethods.POST))
                .serviceCall(BANK_B_EUREKA_SERVICE_NAME + "/api/transactions")
                .convertBodyTo(String.class)
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        exchange.getIn().setHeader("random-y", new Random(System.currentTimeMillis()).nextInt(100));
                    }
                })
                .log("################################ random-y is: ${header[random-x]}")
                .choice()
                    .when(header("random-y").isGreaterThan(90))
                        .throwException(new RuntimeException("Random failure during direct:transfer-bank-b"))
                .end()
                .log("End of direct:transfer-bank-b with body: ${body}");

        from("direct:rollback-transfer-bank-b")
                .setBody(header("tnx")).convertBodyTo(String.class)
                .unmarshal().json(JsonLibrary.Jackson, Transaction.class)
                .log("Start of direct:rollback-transfer-bank-b with body: ${body}")
                .removeHeader("CamelHttp*")
                .removeHeader(Exchange.HTTP_PATH)
                .removeHeader(Exchange.HTTP_URI)
                .setHeader(Exchange.HTTP_METHOD, constant(HttpMethods.DELETE))
                .setHeader("id", simple("${body.id}"))
                .setBody(simple(""))
                .serviceCall(BANK_B_EUREKA_SERVICE_NAME + "/api/transactions/${header[id]}")
                .convertBodyTo(String.class)
                .log("End of direct:rollback-transfer-bank-b with body: ${body}");

    }

}
