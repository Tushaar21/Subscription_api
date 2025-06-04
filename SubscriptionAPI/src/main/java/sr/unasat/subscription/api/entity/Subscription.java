package sr.unasat.subscription.api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "subscriptions")
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "First name is required")
    @Column(nullable = false)
    private String firstname;

    @NotBlank(message = "Last name is required")
    @Column(nullable = false)
    private String lastname;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Phone number is required")
    @Column(nullable = false)
    private String phonenumber;

    @ManyToOne
    @JoinColumn(name = "subscription_type_id", referencedColumnName = "id", nullable = false)
    private Prices price;

    @Column(name = "services")
    private String services; // Comma-separated values for checkboxes

    // Constructors
    public Subscription() {}

    public Subscription(String firstname, String lastname, String email, String phonenumber) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.phonenumber = phonenumber;
    }

    public Subscription(String firstname, String lastname, String email, String phonenumber, Prices price, String services) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.phonenumber = phonenumber;
        this.price = price;
        this.services = services;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getFirstname() { return firstname; }
    public void setFirstname(String firstname) { this.firstname = firstname; }
    public String getLastname() { return lastname; }
    public void setLastname(String lastname) { this.lastname = lastname; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhonenumber() { return phonenumber; }
    public void setPhonenumber(String phonenumber) { this.phonenumber = phonenumber; }
    public Prices getPrice() {
        return price;
    }
    public void setPrice(Prices price) {
        this.price = price;
    }
    public String getServices() { return services; }
    public void setServices(String services) { this.services = services; }
}