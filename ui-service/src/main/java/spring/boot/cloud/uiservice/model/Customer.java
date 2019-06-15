package spring.boot.cloud.uiservice.model;

public class Customer {

    private Long id;

    private Long cifNumber;

    private String firstName;

    private String lastName;

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

    public Customer(Long id, Long cifNumber, String firstName, String lastName) {
        this.id = id;
        this.cifNumber = cifNumber;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Customer() {
    }
}
