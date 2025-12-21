package org.example.Entity;

import jakarta.persistence.*;
import jakarta.persistence.Transient;
import javafx.beans.property.*;
import javafx.collections.ObservableList;

import static javafx.collections.FXCollections.observableArrayList;
import java.util.*;

@Entity
@DiscriminatorValue("PIZZA")
@Access(AccessType.PROPERTY)
public final class Pizza extends Food {
    @Transient
    private ObjectProperty<Builder.Dough> dough;
    @Transient
    private ObjectProperty<Builder.Sauce> sauce;

    @Transient
    private final ListProperty<Builder.Topping> defaultToppings;
    @Transient
    private final ListProperty<Builder.Topping> customToppings;
    @Transient
    private final ListProperty<Builder.Topping> deletedToppings;

    public Pizza(){
        super();
        this.dough = new SimpleObjectProperty<>();
        this.sauce = new SimpleObjectProperty<>();
        this.defaultToppings = new SimpleListProperty<>(observableArrayList());
        this.customToppings = new SimpleListProperty<>(observableArrayList());
        this.deletedToppings = new SimpleListProperty<>(observableArrayList());
    }

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

    @Enumerated(EnumType.STRING)
    @Column(name = "dough")
    public Builder.Dough getDough() { return dough.get(); }
    public void setDough(Builder.Dough dough) { 
        if (this.dough == null) this.dough = new SimpleObjectProperty<>();
        this.dough.set(dough); }
    @Transient
    public ObjectProperty<Builder.Dough> doughProperty() { return dough; }

    @Enumerated(EnumType.STRING)
    @Column(name = "sauce")
    public Builder.Sauce getSauce() { return sauce.get(); }
    public void setSauce(Builder.Sauce sauce) { 
        if (this.sauce == null) this.sauce = new SimpleObjectProperty<>();
        this.sauce.set(sauce); }
    @Transient
    public ObjectProperty<Builder.Sauce> sauceProperty() { return sauce; }

    @ElementCollection(fetch = FetchType.EAGER)
    @Column(name = "default_toppings")
    public ObservableList<Builder.Topping> getDefaultToppings() { return defaultToppings.get(); }
    @Transient
    public ListProperty<Builder.Topping> defaultToppingsProperty() { return defaultToppings; }

    public ObservableList<Builder.Topping> getCustomToppings() { return customToppings.get(); }
    @Transient
    public ListProperty<Builder.Topping> customToppingsProperty() { return customToppings; }

    public ObservableList<Builder.Topping> getDeletedToppings() { return deletedToppings.get(); }
    @Transient
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
