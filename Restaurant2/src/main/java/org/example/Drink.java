package org.example;

import javafx.beans.property.*;

public final class Drink extends Product {
    private IntegerProperty volume;

    public Drink(String name, double price, Category category, int volume) {
        super(name, price, category);
        this.volume = new SimpleIntegerProperty(volume);
    }

    public int getVolume() { return volume.get(); }
    public void setVolume(int volume) { this.volume.set(volume); }
    public IntegerProperty volumeProperty() { return volume; }
}
