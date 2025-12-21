package org.example;

import javafx.beans.property.*;

import java.util.Objects;

public abstract sealed class Product permits Food, Drink {
    private StringProperty name;
    private DoubleProperty price;
    public static final double tax = 0.09;

    public enum Category{
        APPETIZER,
        MAIN_COURSE,
        DESSERT,
        SOFT_DRINK,
        ALCOHOLIC_DRINK
    };
    private ObjectProperty<Category> category;

    public Product(String name, double price, Category category) {
        this.name = new SimpleStringProperty(name);
        this.price = new SimpleDoubleProperty(price * (1 + tax));
        this.category = new SimpleObjectProperty<>(category);
    }

    public String getName() { return name.get(); }
    public void setName(String name) { this.name.set(name); }
    public StringProperty nameProperty() { return name; }

    public double getPrice() { return price.get(); }
    public void setPrice(double price) { this.price.set(price); }
    public DoubleProperty priceProperty() { return price; }

    public Category getCategory() { return category.get(); }
    public void setCategory(Category category) { this.category.set(category); }
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

