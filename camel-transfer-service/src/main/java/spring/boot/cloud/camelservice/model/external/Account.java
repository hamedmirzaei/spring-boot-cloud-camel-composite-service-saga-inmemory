package spring.boot.cloud.camelservice.model.external;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

public class Account implements Serializable {

    private Long id;

    private String accountNumber;

    private Long customerId;

    private Long balance;

    private String status;

    public Account() {
    }

    public Account(String accountNumber, Long customerId, Long balance, String status) {
        this.accountNumber = accountNumber;
        this.customerId = customerId;
        this.balance = balance;
        this.status = status;
    }

    public Account(Long id, String accountNumber, Long customerId, Long balance, String status) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.customerId = customerId;
        this.balance = balance;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getBalance() {
        return balance;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", accountNumber='" + accountNumber + '\'' +
                ", customerId=" + customerId +
                ", balance=" + balance +
                ", status='" + status + '\'' +
                '}';
    }
}
