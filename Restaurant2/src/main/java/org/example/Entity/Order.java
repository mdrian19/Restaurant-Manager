package org.example.Entity;

import jakarta.persistence.*;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Access(AccessType.PROPERTY)
public class Order {
    private LongProperty id = new SimpleLongProperty();
    private ObjectProperty<LocalDateTime> date = new SimpleObjectProperty<>();
    private DoubleProperty total = new SimpleDoubleProperty(0.0);
    private StringProperty status = new SimpleStringProperty("OPEN");

    private ObjectProperty<User> user = new SimpleObjectProperty<>();
    private List<OrderItem> items = new ArrayList<>();

    public Order() {
        this.date.set(LocalDateTime.now());
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() { return id.get(); }
    public void setId(Long id) { this.id.set(id); }
    public LongProperty idProperty() { return id; }

    @Column(name = "order_date")
    public LocalDateTime getDate() { return date.get(); }
    public void setDate(LocalDateTime date) { this.date.set(date); }
    public ObjectProperty<LocalDateTime> dateProperty() { return date; }

    @Column
    public double getTotal() { return total.get(); }
    public void setTotal(double total) { this.total.set(total); }
    public DoubleProperty totalProperty() { return total; }

    @Column
    public String getStatus() { return status.get(); }
    public void setStatus(String status) { this.status.set(status); }
    public StringProperty statusProperty() { return status; }

    @ManyToOne
    @JoinColumn(name = "user_id")
    public User getUser() { return user.get(); }
    public void setUser(User user) { this.user.set(user); }
    public ObjectProperty<User> userProperty() { return user; }

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }

    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }

    public void removeItem(OrderItem item) {
        items.remove(item);
        item.setOrder(null);
    }
}
