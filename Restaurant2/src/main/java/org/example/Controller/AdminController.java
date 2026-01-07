package org.example.Controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.Entity.*;
import org.example.Entity.User.Role;
import org.example.Service.OrderService;
import org.example.Service.ProductService;
import org.example.Service.UserService;
import org.example.Service.OfferService;

import java.time.format.DateTimeFormatter;

public class AdminController {
    @FXML private TableView<User> staffTable;
    @FXML private TableColumn<User, String> staffName;
    @FXML private TableColumn<User, String> staffRole;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    @FXML private TableView<Product> menuTable;
    @FXML private TableColumn<Product, String> prodName;
    @FXML private TableColumn<Product, Double> prodPrice;
    @FXML private TableColumn<Product, String> prodCategory;
    @FXML private TextField prodNameField;
    @FXML private TextField prodPriceField;
    @FXML private ComboBox<Product.Category> prodCategoryField;
    @FXML private CheckBox prodVegetarianField;

    @FXML private CheckBox happyHourCheck;
    @FXML private CheckBox valentinesDayCheck;
    @FXML private CheckBox freeBeerCheck;

    @FXML private TableView<Order> ordersHistoryTable;
    @FXML private TableColumn<Order, Long> orderId;
    @FXML private TableColumn<Order, String> orderDate;
    @FXML private TableColumn<Order, String> orderUser;
    @FXML private TableColumn<Order, Double> orderTotal;

    private final UserService userService = new UserService();
    private final ProductService productService = new ProductService();
    private final OfferService offerService = new OfferService();
    private final OrderService orderService = new OrderService();

    @FXML
    public void initialize(){
        try {
            setupStaffTable();
            refreshStaffTable();

            setupMenuTable();
            refreshMenuTable();
            prodCategoryField.setItems(FXCollections.observableArrayList(Product.Category.values()));

            setupOffers();

            setupHistoryTable();
            refreshHistoryTable();
        } catch (Exception e) {
            showAlert("Error initializing admin interface: " + e.getMessage());
        }
    }

    private void setupStaffTable(){
        staffName.setCellValueFactory(cell -> cell.getValue().usernameProperty());
        staffRole.setCellValueFactory(cell -> cell.getValue().roleProperty().asString());
    }

    private void refreshStaffTable(){
        staffTable.setItems(FXCollections.observableArrayList(userService.getUsersByRole(Role.STAFF)));
    }

    @FXML
    private void handleAddStaff(){
        String username = usernameField.getText();
        String password = passwordField.getText();

        try{
            userService.registerUser(username, password, Role.STAFF);
            refreshStaffTable();
            usernameField.clear();
            passwordField.clear();
        } catch (Exception e) {
            showAlert("Error adding staff: " + e.getMessage());
        }
    }


    @FXML
    private void handleDeleteStaff(){
        User selected = staffTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            userService.deleteUser(selected);
            refreshStaffTable();
        }
    }

    private void setupMenuTable(){
        prodName.setCellValueFactory(cell -> cell.getValue().nameProperty());
        prodPrice.setCellValueFactory(cell -> cell.getValue().priceProperty().asObject());
        prodCategory.setCellValueFactory(cell -> cell.getValue().categoryStringProperty());

        menuTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                prodNameField.setText(newSelection.getName());
                prodPriceField.setText(String.valueOf(newSelection.getPrice()));
                prodCategoryField.setValue(newSelection.getCategory());
                prodVegetarianField.setSelected(newSelection.isVegetarian());
            }
        });
    }

    @FXML
    private void handleAddProduct(){
        try{
            String name = prodNameField.getText();
            double price = Double.parseDouble(prodPriceField.getText());
            Product.Category category = prodCategoryField.getValue();
            boolean isVegetarian = prodVegetarianField.isSelected();

            Product newProduct;

            if (category == Product.Category.SOFT_DRINK || category == Product.Category.ALCOHOLIC_DRINK) {
                newProduct = new Drink(name, price, category, 500);
            } else {
                newProduct = new Food(name, price, category, 500, isVegetarian);
            }

            productService.addProduct(newProduct);
            refreshMenuTable();
            prodNameField.clear();
            prodPriceField.clear();
            prodCategoryField.setValue(null);
            prodVegetarianField.setSelected(false);
        } catch (Exception e) {
            showAlert("Error adding product: " + e.getMessage());
        }
    }

    @FXML
    private void handleClearForm(){
        prodNameField.clear();
        prodPriceField.clear();
        prodCategoryField.setValue(null);
        prodVegetarianField.setSelected(false);
        menuTable.getSelectionModel().clearSelection();
    }

    private void refreshMenuTable(){
        menuTable.setItems(FXCollections.observableArrayList(productService.getAllProducts()));
    }

    private void setupOffers(){
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
    }

    private void setupHistoryTable(){
        orderId.setCellValueFactory(new PropertyValueFactory<>("id"));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        orderDate.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getDate().format(formatter)
        ));

        orderUser.setCellValueFactory(cell -> {
            if (cell.getValue().getUser() != null) {
                return new SimpleStringProperty(cell.getValue().getUser().getUsername());
            } else {
                return new SimpleStringProperty("Guest");
            }
        });

        orderTotal.setCellValueFactory(cell -> cell.getValue().totalProperty().asObject());
    }

    private void refreshHistoryTable(){
        User user = new User();
        user.setRole(Role.ADMIN);
        ordersHistoryTable.setItems(FXCollections.observableArrayList(orderService.getOrdersHistory(user)));
    }

    private void showAlert(String message){
        new Alert(Alert.AlertType.ERROR, message).show();
    }
}
