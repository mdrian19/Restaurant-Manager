package org.example.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.Entity.User;
import org.example.Service.AuthenticationService;

import java.util.Optional;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Button guestButton;

    private final AuthenticationService authenticationService = new AuthenticationService();

    public LoginController() {
    }

    @FXML
    private void initialize() {
        loginButton.setOnAction(event -> handleLogin());
        guestButton.setOnAction(event -> loadGuestInterface());
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Username and password cannot be empty.");
            return;
        }

        try{
            User user = authenticationService.authenticate(username, password).orElse(null);
            if (user != null) {
                switch(user.getRole()){
                    case ADMIN -> loadInterface("/org/example/View/Admin.fxml", "Admin Dashboard", user);
                    case STAFF -> loadInterface("/org/example/View/Staff.fxml", "Staff Dashboard", user);
                    default -> showAlert("Unknown user role.");
                }
            }
            else{
                showAlert("Invalid username or password.");
            }
        } catch (Exception e) {
            showAlert("An error occurred during authentication.");
        }
    }

    @FXML
    private void loadGuestInterface() {
        loadInterface("/org/example/View/Guest.fxml", "Guest Menu", null);
    }

    private void loadInterface(String fxmlFile, String title, User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            if (user != null){
                Object controller = loader.getController();
                if (controller instanceof StaffController)
                    ((StaffController) controller).initializeData(user);
            }

            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setTitle("La Andrei - " + title);
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
        } catch (Exception e) {
            showAlert("Failed to load the interface.");
        }
    }

    private void showAlert(String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
