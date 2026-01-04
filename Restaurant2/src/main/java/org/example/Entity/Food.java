package org.example.Entity;

import jakarta.persistence.*;
import jakarta.persistence.Transient;
import javafx.beans.property.*;

@Entity
@DiscriminatorValue("FOOD")
@Access(AccessType.PROPERTY)
public class Food extends Product {
    @Transient
    private IntegerProperty weight;

    @Transient
    private BooleanProperty isVegetarian;

    public Food(){
        super();
        this.weight = new SimpleIntegerProperty(0);
        this.isVegetarian = new SimpleBooleanProperty(false);
    }

    public Food(String name, double price, Category category, int weight, boolean isVegetarian) {
        super(name, price, category);
        this.weight = new SimpleIntegerProperty(weight);
        this.isVegetarian = new SimpleBooleanProperty(isVegetarian);
    }

    @Column(name = "weight")
    public int getWeight() { return weight.get(); }
    public void setWeight(int weight) { 
        if (this.weight == null) this.weight = new SimpleIntegerProperty();
        this.weight.set(weight); }
    @Transient
    public IntegerProperty weightProperty() { return weight; }

    @Column(name = "is_vegetarian")
    public boolean getVegetarian() { return isVegetarian.get(); }
    public void setVegetarian(boolean vegetarian) { 
        if (this.isVegetarian == null) this.isVegetarian = new SimpleBooleanProperty();
        isVegetarian.set(vegetarian); }
    @Transient
    public BooleanProperty isVegetarianProperty() { return isVegetarian; }
}
