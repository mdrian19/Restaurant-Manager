package org.example.Entity;

import jakarta.persistence.*;
import javafx.beans.property.*;

@Entity
@Table (name = "users")
@Access(AccessType.PROPERTY)
public class User {
    public enum Role {
        ADMIN,
        STAFF,
        GUEST
    }

    private LongProperty id = new SimpleLongProperty();
    private StringProperty username = new SimpleStringProperty();
    private StringProperty password = new SimpleStringProperty();
    private ObjectProperty<Role> role = new SimpleObjectProperty<>();

    public User () {}

    public User(String username, String password, Role role) {
        this.username.set(username);
        this.password.set(password);
        this.role.set(role);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() { return id.get(); }
    public void setId(Long id) { this.id.set(id); }
    public LongProperty idProperty() { return id; }

    @Column(unique = true, nullable = false)
    public String getUsername() { return username.get(); }
    public void setUsername(String username) { this.username.set(username); }
    public StringProperty usernameProperty() { return username; }

    @Column(nullable = false)
    public String getPassword() { return password.get(); }
    public void setPassword(String password) { this.password.set(password); }
    public StringProperty passwordProperty() { return password; }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public Role getRole() { return role.get(); }
    public void setRole(Role role) { this.role.set(role); }
    public ObjectProperty<Role> roleProperty() { return role; }

    @Override
    public String toString() { return getUsername(); }

}
