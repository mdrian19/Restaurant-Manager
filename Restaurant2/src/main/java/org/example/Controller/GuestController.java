package org.example.Controller;

import javafx.fxml.FXML;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.Entity.*;
import org.example.Service.ProductService;
import java.util.List;
import java.util.stream.Collectors;

public class GuestController {
    @FXML
    private TableView<Product> productsTable;

    @FXML
    private TextField searchField;

    @FXML
    private CheckBox vegetarianFilter;

    @FXML
    private ComboBox<String> categoryFilter;

    @FXML
    private Slider priceSlider;

    @FXML
    private Label detailsLabel;

    @FXML
    private Button loginButton;

    private final ProductService productService = new ProductService();
    private List<Product> allProducts;

    @FXML
    private void initialize(){
        TableColumn<Product,String> nameColumn = new TableColumn<>("Produs");
        nameColumn.setCellValueFactory(cell -> cell.getValue().nameProperty());

        TableColumn<Product,Number> priceColumn = new TableColumn<>("Pret (RON)");
        priceColumn.setCellValueFactory(cell -> cell.getValue().priceProperty());

        TableColumn<Product,String> categoryColumn = new TableColumn<>("Categorie");
        categoryColumn.setCellValueFactory(cell -> cell.getValue().categoryStringProperty());

        productsTable.getColumns().setAll(nameColumn, priceColumn, categoryColumn);

        allProducts = productService.getAllProducts().stream()
                .filter(p -> !p.getName().equalsIgnoreCase("Bere gratis"))
                .collect(Collectors.toList());
        updateTable(allProducts);

        categoryFilter.setItems(FXCollections.observableArrayList("Toate",
                "Aperitiv",
                "Fel principal",
                "Desert",
                "Bautura racoritoare",
                "Bautura alcoolica"));
        categoryFilter.getSelectionModel().selectFirst();

        vegetarianFilter.selectedProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if(!newValue.equals(oldValue))
                        applyFilters();
                });
        categoryFilter.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if(!newValue.equals(oldValue))
                        applyFilters();
                });
        priceSlider.valueProperty().addListener((
                observable, oldValue, newValue) -> {
                if(!newValue.equals(oldValue))
                    applyFilters();});

        productsTable.getSelectionModel().selectedItemProperty().addListener((observable, oldSelection, newSelection) -> {
            if (newSelection != null) {
                showDetails(newSelection);
            }
        });
    }

    @FXML
    private void handleSearch(){
        String query = searchField.getText();
        productService.findByName(query).ifPresent(p -> {
            updateTable(List.of(p));
            showDetails(p);
        });
    }

    private void applyFilters(){
        boolean isVegetarian = vegetarianFilter.isSelected();
        String category = categoryFilter.getValue();
        double price = priceSlider.getValue();

        List<Product> filtered = productService.filterProducts(allProducts, isVegetarian, category, price);
        updateTable(filtered);
    }

    private void updateTable(List<Product> products){
        productsTable.setItems(FXCollections.observableArrayList(products));
    }

    private void showDetails(Product p){
        StringBuilder sb = new StringBuilder();
        sb.append("Nume: ").append(p.getName()).append("\n");
        sb.append("Preț: ").append(p.getPrice()).append(" RON\n");
        sb.append("Categorie: ").append(p.categoryStringProperty().get()).append("\n");

        switch (p) {
            case Pizza pizza -> {
                sb.append("--- Detalii Pizza ---\n");
                sb.append("Blat: ").append(pizza.getDoughString()).append("\n");
                sb.append("Sos: ").append(pizza.getSauceString()).append("\n");
                sb.append("Topping-uri: ").append(pizza.getToppingsString()).append("\n");
                sb.append("Gramaj: ").append(pizza.getWeight()).append("g\n");
                sb.append("Vegetarian: ").append(pizza.isVegetarian() ? "DA" : "NU").append("\n\n");
            }
            case Food food -> {
                sb.append("Gramaj: ").append(food.getWeight()).append("g\n");
                sb.append("Vegetarian: ").append(food.isVegetarian() ? "DA" : "NU").append("\n");
            }
            case Drink drink -> sb.append("Volum: ").append(drink.getVolume()).append("ml\n");
            default -> {
            }
        }

        detailsLabel.setText(sb.toString());
    }

    @FXML
    private void handleLoginRedirect(){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/View/Login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setTitle("La Andrei - Login");
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
        } catch(Exception e){
            new Alert(Alert.AlertType.ERROR, "Failed to load the login interface.").show();
        }
    }
}
