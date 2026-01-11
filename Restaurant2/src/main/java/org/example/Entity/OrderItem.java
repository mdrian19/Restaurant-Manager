package org.example.Entity;

import jakarta.persistence.*;
import javafx.beans.property.*;

@Entity
@Table(name = "order_items")
@Access(AccessType.PROPERTY)
public class OrderItem {
    private LongProperty id = new SimpleLongProperty();
    private IntegerProperty quantity = new SimpleIntegerProperty();

    private ObjectProperty<Product> product = new SimpleObjectProperty<>();
    private ObjectProperty<Order> order = new SimpleObjectProperty<>();

    public OrderItem() {}

    public OrderItem(Product product, int quantity) {
        this.product.set(product);
        this.quantity.set(quantity);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() { return id.get(); }
    public void setId(Long id) { this.id.set(id); }
    public LongProperty idProperty() { return id; }

    @Column(nullable = false)
    public int getQuantity() { return quantity.get(); }
    public void setQuantity(int quantity) { this.quantity.set(quantity); }
    public IntegerProperty quantityProperty() { return quantity; }

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    public Product getProduct() { return product.get(); }
    public void setProduct(Product product) { this.product.set(product); }
    public ObjectProperty<Product> productProperty() { return product; }

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    public Order getOrder() { return order.get(); }
    public void setOrder(Order order) { this.order.set(order); }
    public ObjectProperty<Order> orderProperty() { return order; }

    @Transient
    public DoubleProperty subtotalProperty() {
        if (getProduct() != null) {
            return new SimpleDoubleProperty(getProduct().getPrice() * getQuantity());
        }
        return new SimpleDoubleProperty(0.0);
    }

    @Transient
    public double getPrice() {
        if (this.product != null) {
            return this.product.get().getPrice() * ((double) this.quantity.get());
        }
        return 0.0;
    }
}
