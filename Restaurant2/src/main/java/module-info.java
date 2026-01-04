module org.example.restaurant {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;
    requires jakarta.persistence;
    requires org.hibernate.orm.core;
    requires java.sql;

    uses jakarta.persistence.spi.PersistenceProvider;

    opens org.example.Controller to javafx.fxml;
    opens org.example to javafx.fxml, com.fasterxml.jackson.databind;
    exports org.example;
    exports org.example.Entity;
    opens org.example.Entity to org.hibernate.orm.core, javafx.base;
}