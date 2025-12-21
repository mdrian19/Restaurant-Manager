module org.example.restaurant2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;

    opens org.example to javafx.fxml, com.fasterxml.jackson.databind;
    exports org.example;
}