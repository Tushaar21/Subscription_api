package sr.unasat.subscription.api.dto;

import java.math.BigDecimal;

public class PricesDTO {

    private int id;
    private String subscriptionType;
    private BigDecimal price;
    private String description;

    public PricesDTO() {
    }

    public PricesDTO(int id, String subscriptionType, BigDecimal price, String description) {
        this.id = id;
        this.subscriptionType = subscriptionType;
        this.price = price;
        this.description = description;
    }

    public PricesDTO(String subscriptionType, BigDecimal price, String description) {
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
        return "PricesDTO{" +
                "id=" + id +
                ", subscriptionType='" + subscriptionType + '\'' +
                ", price=" + price +
                ", description='" + description + '\'' +
                '}';
    }
}
