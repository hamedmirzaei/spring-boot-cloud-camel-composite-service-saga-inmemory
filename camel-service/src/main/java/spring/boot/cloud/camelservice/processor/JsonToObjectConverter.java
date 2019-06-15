package spring.boot.cloud.camelservice.processor;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import spring.boot.cloud.camelservice.model.Customer;
import spring.boot.cloud.camelservice.model.Transaction;

import java.util.List;

public class JsonToObjectConverter implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        Gson gson = new Gson();
        List<Exchange> list = exchange.getProperty(Exchange.GROUPED_EXCHANGE, List.class);

        String customersStr = list.get(0).getIn().getBody(String.class);
        List<Customer> customers =
                gson.fromJson(customersStr, new TypeToken<List<Customer>>(){}.getType());

        String transactionsStr = list.get(1).getIn().getBody(String.class);
        List<Transaction> transactions =
                gson.fromJson(transactionsStr, new TypeToken<List<Transaction>>(){}.getType());

        exchange.getIn().setBody(customers + " , " +  transactions);
    }
}
