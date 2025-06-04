package sr.unasat.subscription.api.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "prices")
public class Prices {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name= "subscription_type", nullable = false, unique = true)
    private String subscriptionType;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "price")
    private List<Subscription> subscriptions;

    public Prices() {
    }

    public Prices(int id, String subscriptionType, BigDecimal price, String description) {
        this.id = id;
        this.subscriptionType = subscriptionType;
        this.price = price;
        this.description = description;
    }

    public Prices(String subscriptionType, BigDecimal price, String description) {
        this.subscriptionType = subscriptionType;
        this.price = price;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSubscriptionType() {
        return subscriptionType;
    }

    public void setSubscriptionType(String subscriptionType) {
        this.subscriptionType = subscriptionType;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Prices{" +
                "id=" + id +
                ", subscriptionType='" + subscriptionType + '\'' +
                ", price=" + price +
                ", description='" + description + '\'' +
                '}';
    }
}
