package org.example.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;

@Entity
@Table(name = "offers")
public class Offer {
    @Id
    private String name;
    private boolean active;

    public Offer() {}

    public Offer(String name, boolean active) {
        this.name = name;
        this.active = active;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
