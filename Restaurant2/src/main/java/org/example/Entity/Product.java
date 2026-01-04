package org.example.Entity;

import jakarta.persistence.*;
import jakarta.persistence.Transient;
import javafx.beans.property.*;

import java.util.Objects;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "product_type", discriminatorType = DiscriminatorType.STRING)
@Access(AccessType.PROPERTY)
public abstract class Product {
    private Long id;

    @Transient
    private StringProperty name;

    @Transient
    private DoubleProperty price;
    public static final double tax = 0.09;

    public enum Category{
        APPETIZER,
        MAIN_COURSE,
        DESSERT,
        SOFT_DRINK,
        ALCOHOLIC_DRINK
    };

    @Transient
    private ObjectProperty<Category> category;

    public Product() {
        this.name = new SimpleStringProperty();
        this.price = new SimpleDoubleProperty();
        this.category = new SimpleObjectProperty<>();
    }

    public Product(String name, double price, Category category) {
        this.name = new SimpleStringProperty(name);
        this.price = new SimpleDoubleProperty(price * (1 + tax));
        this.category = new SimpleObjectProperty<>(category);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    @Column(name = "name")
    public String getName() { return name.get(); }
    public void setName(String name) { 
        if (this.name == null) this.name = new SimpleStringProperty();
        this.name.set(name); }
    @Transient
    public StringProperty nameProperty() { return name; }

    @Column(name = "price")
    public double getPrice() { return price.get(); }
    public void setPrice(double price) { 
        if (this.price == null) this.price = new SimpleDoubleProperty();
        this.price.set(price); }
    @Transient
    public DoubleProperty priceProperty() { return price; }

    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    public Category getCategory() { return category.get(); }
    public void setCategory(Category category) { 
        if (this.category == null) this.category = new SimpleObjectProperty<>();
        this.category.set(category); }
    @Transient
    public ObjectProperty<Category> categoryProperty() { return category; }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(name.get(), product.name.get());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name.get());
    }
}

