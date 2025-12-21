package org.example;

import javafx.beans.property.*;

public sealed class Food extends Product permits Pizza {
    private IntegerProperty weight;
    private BooleanProperty isVegetarian;

    public Food(String name, double price, Category category, int weight, boolean isVegetarian) {
        super(name, price, category);
        this.weight = new SimpleIntegerProperty(weight);
        this.isVegetarian = new SimpleBooleanProperty(isVegetarian);
    }

    public int getWeight() { return weight.get(); }
    public void setWeight(int weight) { this.weight.set(weight); }
    public IntegerProperty weightProperty() { return weight; }

    public boolean isVegetarian() { return isVegetarian.get(); }
    public void setVegetarian(boolean vegetarian) { isVegetarian.set(vegetarian); }
    public BooleanProperty isVegetarianProperty() { return isVegetarian; }
}
