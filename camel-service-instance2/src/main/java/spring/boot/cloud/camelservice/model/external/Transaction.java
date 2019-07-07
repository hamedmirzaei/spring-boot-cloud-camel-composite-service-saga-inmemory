package spring.boot.cloud.camelservice.model.external;

import java.io.Serializable;

public class Transaction implements Serializable {

    private Long id;

    private Long accountId;

    private Long amount;

    public Transaction() {
    }

    public Transaction(Long accountId, Long amount) {
        this.accountId = accountId;
        this.amount = amount;
    }

    public Transaction(Long id, Long accountId, Long amount) {
        this.id = id;
        this.accountId = accountId;
        this.amount = amount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", accountId=" + accountId +
                ", amount=" + amount +
                '}';
    }
}
