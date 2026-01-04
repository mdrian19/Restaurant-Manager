package org.example.Controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.Entity.User;
import org.example.Entity.User.Role;
import org.example.Service.UserService;
import org.example.Service.OfferService;

public class AdminController {
    @FXML
    private TableView<User> staffTable;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private CheckBox happyHourCheck;

    @FXML
    private CheckBox valentinesDayCheck;

    @FXML
    private CheckBox freeBeerCheck;

    private final UserService userService = new UserService();
    private final OfferService offerService = new OfferService();

    @FXML
    public void initialize(){
        try {
            refreshStaffTable();
            happyHourCheck.setSelected(offerService.isHappyHourActive());
            valentinesDayCheck.setSelected(offerService.isValentinesDayActive());
            freeBeerCheck.setSelected(offerService.isFreeBeerActive());

            happyHourCheck.selectedProperty().addListener((obs, oldVal, newVal) -> {
                offerService.setHappyHourActive(newVal);
            });

            valentinesDayCheck.selectedProperty().addListener((obs, oldVal, newVal) -> {
                offerService.setValentinesDayActive(newVal);
            });

            freeBeerCheck.selectedProperty().addListener((obs, oldVal, newVal) -> {
                offerService.setFreeBeerActive(newVal);
            });
        } catch (Exception e) {
            showAlert("Error initializing admin interface: " + e.getMessage());
        }
    }

    @FXML
    private void handleAddStaff(){
        String username = usernameField.getText();
        String password = passwordField.getText();

        try{
            userService.registerUser(username, password, Role.STAFF);
            refreshStaffTable();
            clearFields();
        } catch (Exception e) {
            showAlert("Error adding staff: " + e.getMessage());
        }
    }

    @FXML
    private void handleDeleteStaff(){
        User selectedStaff = staffTable.getSelectionModel().getSelectedItem();
        if (selectedStaff == null) {
            showAlert("No staff member selected.");
            return;
        }

        try{
            userService.deleteUser(selectedStaff);
            refreshStaffTable();
        } catch (Exception e) {
            showAlert("Error deleting staff: " + e.getMessage());
        }
    }

    private void refreshStaffTable(){
        staffTable.setItems(FXCollections.observableArrayList(userService.getUsersByRole(Role.STAFF)));
    }

    private void showAlert(String message){
        new Alert(Alert.AlertType.ERROR, message).show();
    }

    private void clearFields(){
        usernameField.clear();
        passwordField.clear();
    }
}
