module org.example.restaurant2 {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.restaurant2 to javafx.fxml;
    exports org.example.restaurant2;
}