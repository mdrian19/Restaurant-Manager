package org.example.Entity;

import jakarta.persistence.*;
import jakarta.persistence.Transient;
import javafx.beans.property.*;
import javafx.collections.FXCollections;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@DiscriminatorValue("PIZZA")
@Access(AccessType.PROPERTY)
public final class Pizza extends Food {
    @Transient
    private final ObjectProperty<Builder.Dough> dough = new SimpleObjectProperty<>();

    @Transient
    private final ObjectProperty<Builder.Sauce> sauce = new SimpleObjectProperty<>();

    @Transient
    private final ListProperty<Builder.Topping> toppings = new SimpleListProperty<>(FXCollections.observableArrayList());

    public Pizza(){
        super();
    }

    public Pizza(String name, double price, Category category, int weight, boolean isVegetarian,
                 Builder.Dough dough, Builder.Sauce sauce,
                 List<Builder.Topping> toppings) {
        super(name, price, category, weight, isVegetarian);
        this.dough.set(dough);
        this.sauce.set(sauce);
        if (toppings != null) {
            this.toppings.addAll(toppings);
        }
    }

    private Pizza (Builder builder) {
        super(builder.name, builder.price, builder.category, builder.weight, builder.isVegetarian);
        this.dough.set(builder.dough);
        this.sauce.set(builder.sauce);
        this.toppings.addAll(builder.toppings);
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "dough")
    public Builder.Dough getDough() {
        return dough.get();
    }
    public void setDough(Builder.Dough d) {
        this.dough.set(d);
    }
    public ObjectProperty<Builder.Dough> doughProperty() {
        return dough;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "sauce")
    public Builder.Sauce getSauce() {
        return sauce.get();
    }
    public void setSauce(Builder.Sauce s) {
        this.sauce.set(s);
    }

    public ObjectProperty<Builder.Sauce> sauceProperty() {
        return sauce;
    }

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "pizza_default_toppings", joinColumns = @JoinColumn(name = "pizza_id"))
    @Column(name = "topping")
    @Enumerated(EnumType.STRING)
    public List<Builder.Topping> getToppings() {
        return toppings.get();
    }
    public void setToppings(List<Builder.Topping> t) {
        this.toppings.clear();
        if (t != null) this.toppings.addAll(t);
    }

    public ListProperty<Builder.Topping> toppingsProperty() {
        return toppings;
    }

    @Transient
    public String getDoughString(){
        if (getDough() == null) return "";
        return switch(getDough()) {
            case THIN_CRUST -> "Subtire";
            case THICK_CRUST -> "Groasa";
            case STUFFED_CRUST -> "Umpluta";
        };
    }

    @Transient
    public String getSauceString(){
        if (getSauce() == null) return "";
        return switch(getSauce()) {
            case TOMATO -> "Rosii";
            case ALFREDO -> "Alfredo";
            case PESTO -> "Pesto";
        };
    }

    @Transient
    public String getToppingsString(){
        if (getToppings() == null || getToppings().isEmpty()) return "fara topping-uri";

        return getToppings().stream()
                .map(t -> switch (t) {
                    case EXTRA_MOZZARELLA -> "Extra mozzarella";
                    case SALAMI -> "Salam";
                    case MUSHROOMS -> "Ciuperci";
                    case BASIL -> "Busuioc";
                    case OLIVES -> "Masline";
                    case CORN -> "Porumb";
                    case HAM -> "Sunca";
                    case PEPPERS -> "Ardei";
                })
                .collect(Collectors.joining(", "));
    }

    public static class Builder {
        private String name;
        private double price;
        private Category category;
        private int weight;
        private boolean isVegetarian;
        private Dough dough;
        private Sauce sauce;
        private final List<Topping> toppings = new ArrayList<>();

        public Builder setName(String name){ this.name = name; return this; }
        public Builder setPrice(double price){ this.price = price; return this; }
        public Builder setCategory(Category c){ this.category = c; return this; }
        public Builder setWeight(int w){ this.weight = w; return this; }
        public Builder setIsVegetarian(boolean v){ this.isVegetarian = v; return this; }
        public Builder setDough(Dough d){ this.dough = d; return this; }
        public Builder setSauce(Sauce s){ this.sauce = s; return this; }
        public Builder addTopping(Topping t){ this.toppings.add(t); return this; }
        public Builder setToppings(List<Topping> t){
            this.toppings.clear();
            if(t != null) this.toppings.addAll(t);
            return this;
        }

        public Pizza build(){
            return new Pizza(this);
        }

        public enum Dough { THIN_CRUST, THICK_CRUST, STUFFED_CRUST }
        public enum Sauce { TOMATO, ALFREDO, PESTO }
        public enum Topping { EXTRA_MOZZARELLA, SALAMI, MUSHROOMS, BASIL, OLIVES, CORN, HAM, PEPPERS }
    }
}