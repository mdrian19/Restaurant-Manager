package org.example;

import javafx.beans.property.*;
import javafx.collections.ObservableList;

import static javafx.collections.FXCollections.observableArrayList;
import java.util.*;

public final class Pizza extends Food {
    private final ObjectProperty<Builder.Dough> dough;
    private final ObjectProperty<Builder.Sauce> sauce;

    private final ListProperty<Builder.Topping> defaultToppings;
    private final ListProperty<Builder.Topping> customToppings;
    private final ListProperty<Builder.Topping> deletedToppings;

    public Pizza(String name, double price, Category category, int weight, boolean isVegetarian,
                 Builder.Dough dough, Builder.Sauce sauce,
                 List<Builder.Topping> toppings) {
        super(name, price, category, weight, isVegetarian);
        this.dough = new SimpleObjectProperty<>(dough);
        this.sauce = new SimpleObjectProperty<>(sauce);
        this.defaultToppings = new SimpleListProperty<>(observableArrayList(toppings));
        this.customToppings = new SimpleListProperty<>(observableArrayList());
        this.deletedToppings = new SimpleListProperty<>(observableArrayList());

        if (toppings != null) this.defaultToppings.addAll(toppings);
        this.customToppings.addAll(this.defaultToppings);
    }

    private Pizza (Builder builder) {
        super(builder.name, builder.price, builder.category, builder.weight, builder.isVegetarian);

        this.dough = new SimpleObjectProperty<>(builder.dough);
        this.sauce = new SimpleObjectProperty<>(builder.sauce);

        this.defaultToppings = new SimpleListProperty<>(observableArrayList(builder.defaultToppings));
        this.customToppings = new SimpleListProperty<>(observableArrayList(builder.customToppings));
        this.deletedToppings = new SimpleListProperty<>(observableArrayList());
    }

    public Builder.Dough getDough() { return dough.get(); }
    public void setDough(Builder.Dough dough) { this.dough.set(dough); }
    public ObjectProperty<Builder.Dough> doughProperty() { return dough; }

    public Builder.Sauce getSauce() { return sauce.get(); }
    public void setSauce(Builder.Sauce sauce) { this.sauce.set(sauce); }
    public ObjectProperty<Builder.Sauce> sauceProperty() { return sauce; }

    public ObservableList<Builder.Topping> getDefaultToppings() { return defaultToppings.get(); }
    public ListProperty<Builder.Topping> defaultToppingsProperty() { return defaultToppings; }

    public ObservableList<Builder.Topping> getCustomToppings() { return customToppings.get(); }
    public ListProperty<Builder.Topping> customToppingsProperty() { return customToppings; }

    public ObservableList<Builder.Topping> getDeletedToppings() { return deletedToppings.get(); }
    public ListProperty<Builder.Topping> deletedToppingsProperty() { return deletedToppings; }

    public void setCustomToppings(Collection<Builder.Topping> toppings){
        this.customToppings.clear();
        this.customToppings.addAll(toppings);
    }

    public static class Builder {
        private String name;
        private double price;
        private Category category;
        private int weight;
        private boolean isVegetarian;
        private Dough dough;
        private Sauce sauce;
        private final Vector<Topping> defaultToppings = new Vector<>();
        private final Vector<Topping> customToppings = new Vector<>();

        public Builder setName(String name){ this.name = name; return this; }
        public Builder setPrice(double price){ this.price = price; return this; }
        public Builder setCategory(Category c){ this.category = c; return this; }
        public Builder setWeight(int w){ this.weight = w; return this; }
        public Builder setIsVegetarian(boolean v){ this.isVegetarian = v; return this; }
        public Builder setDough(Dough d){ this.dough = d; return this; }
        public Builder setSauce(Sauce s){ this.sauce = s; return this; }
        public Builder addDefaultTopping(Topping t){ this.defaultToppings.add(t); return this; }
        public Builder addCustomTopping(Topping t){ this.customToppings.add(t); return this; }

        public Pizza build(){
            if (customToppings.isEmpty()) customToppings.addAll(defaultToppings);
            Pizza p = new Pizza(this);
            return p;
        }

        public enum Dough { THIN_CRUST, THICK_CRUST, STUFFED_CRUST }
        public enum Sauce { TOMATO, ALFREDO, PESTO }
        public enum Topping { EXTRA_MOZZARELLA, SALAMI, MUSHROOMS, BASIL, OLIVES, CORN, HAM, PEPPERS }
    }

}
