package org.example.Entity;

import jakarta.persistence.*;
import jakarta.persistence.Transient;
import javafx.beans.property.*;

@Entity
@DiscriminatorValue("DRINK")
@Access(AccessType.PROPERTY)
public final class Drink extends Product {
    @Transient
    private IntegerProperty volume = new SimpleIntegerProperty();

    public Drink(){
        super();
    }

    public Drink(String name, double price, Category category, int volume) {
        super(name, price, category, true);
        this.volume.set(volume);
    }

    @Column(name = "volume")
    public int getVolume() { return volume.get(); }
    public void setVolume(int volume) { 
        if (this.volume == null) this.volume = new SimpleIntegerProperty();
        this.volume.set(volume); 
    }
    @Transient
    public IntegerProperty volumeProperty() { return volume; }

    @Override
    @Transient
    public boolean isVegetarian() {
        return true;
    }
}
