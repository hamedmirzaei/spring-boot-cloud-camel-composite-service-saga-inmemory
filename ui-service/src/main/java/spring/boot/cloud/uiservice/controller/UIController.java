package spring.boot.cloud.uiservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import spring.boot.cloud.uiservice.model.Customer;

import java.util.List;

@Controller
public class UIController {

    private final String EUREKA_CLIENT_SERVICE_ID = "spring-cloud-eureka-client";

    @Autowired
    private DiscoveryClient discoveryClient;

    @GetMapping("/customers")
    private String handleRequest(Model model) {
        List<ServiceInstance> instances = discoveryClient.getInstances(EUREKA_CLIENT_SERVICE_ID);
        if (instances != null && instances.size() > 0) {//todo: replace with a load balancing mechanism
            ServiceInstance serviceInstance = instances.get(0);
            String url = serviceInstance.getUri().toString();
            url = url + "/customers";

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<List<Customer>> customerResponse = restTemplate.exchange(
                    url, HttpMethod.GET, null,
                    new ParameterizedTypeReference<List<Customer>>() {
                    });
            List<Customer> customers = customerResponse.getBody();

            model.addAttribute("customers", customers);
        }
        return "index";
    }

}
