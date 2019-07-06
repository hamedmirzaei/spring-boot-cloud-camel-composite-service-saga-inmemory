package spring.boot.cloud.eurekaclient.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spring.boot.cloud.eurekaclient.model.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
