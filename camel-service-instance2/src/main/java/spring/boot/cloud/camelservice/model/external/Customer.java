package spring.boot.cloud.camelservice.model.external;

import java.io.Serializable;

public class Customer implements Serializable {

    private Long id;

    private Long cifNumber;

    private String firstName;

    private String lastName;

    public Customer() {
    }

    public Customer(Long cifNumber, String firstName, String lastName) {
        this.cifNumber = cifNumber;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Customer(Long id, Long cifNumber, String firstName, String lastName) {
        this.id = id;
        this.cifNumber = cifNumber;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCifNumber() {
        return cifNumber;
    }

    public void setCifNumber(Long cifNumber) {
        this.cifNumber = cifNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", cifNumber=" + cifNumber +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}
