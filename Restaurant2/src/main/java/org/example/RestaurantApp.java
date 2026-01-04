package org.example;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.util.converter.NumberStringConverter;
import javafx.stage.FileChooser;
import org.example.Entity.*;

import java.io.File;

public class RestaurantApp extends Application {
    private TextField nameField;
    private TextField priceField;
    private TextField categoryField;
    private TextArea detailsArea;

    private Menu menu;
    private Stage primaryStage;
    private Product selectedProduct = null;

    private org.example.Menu restaurantMenu;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        restaurantMenu = new org.example.Menu();

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(15));

        VBox leftPane = new VBox(8);
        Label listLabel = new Label("Products list");
        listLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        ListView<Product> products = new ListView<>();
        products.setItems(restaurantMenu.getAllProductsObservable());

        products.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Product item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else setText(item.getName());
            }
        });

        VBox.setVgrow(products, Priority.ALWAYS);
        leftPane.getChildren().addAll(listLabel, products);
        root.setLeft(leftPane);

        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(15);
        formGrid.setPadding(new Insets(0, 0, 0, 20));
        formGrid.setAlignment(Pos.TOP_LEFT);

        Label detailsTitle = new Label("Details");
        detailsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        formGrid.add(detailsTitle, 0, 0, 2, 1);

        nameField = new TextField();
        priceField = new TextField();
        categoryField = new TextField();
        categoryField.setEditable(false);

        detailsArea = new TextArea();
        detailsArea.setEditable(false);
        detailsArea.setWrapText(true);
        detailsArea.setPrefRowCount(4);

        formGrid.add(new Label("Name:"), 0, 1);
        formGrid.add(nameField, 1, 1);

        formGrid.add(new Label("Price (RON):"), 0, 2);
        formGrid.add(priceField, 1, 2);

        formGrid.add(new Label("Category:"), 0, 3);
        formGrid.add(categoryField, 1, 3);

        formGrid.add(new Label("Specifications:"), 0, 4);
        formGrid.add(detailsArea, 1, 4);

        root.setCenter(formGrid);

        products.getSelectionModel().selectedItemProperty().addListener((obs, oldProduct, newProduct) -> {
            if (newProduct != null) {
                bindingProcedure(newProduct);
            }
        });

        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu();

        MenuItem exportItem = new MenuItem("Export to JSON");
        exportItem.setOnAction(e -> exportToJson());

        MenuItem importItem = new MenuItem("Import from JSON");
        importItem.setOnAction(e -> importFromJson());

        fileMenu.getItems().addAll(exportItem, importItem);
        menuBar.getMenus().add(fileMenu);
        root.setTop(menuBar);

        Scene scene = new Scene(root, 1000, 650);
        primaryStage.setTitle("Restaurant \"La Andrei\"");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void exportToJson() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save menu to JSON");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("JSON Files", "*.json")
        );
        var selectedFile = fileChooser.showSaveDialog(primaryStage);
        if (selectedFile != null) {
            restaurantMenu.exportDataToJSON(selectedFile);
        }
    }

    private void importFromJson() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import Menu from JSON");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("JSON Files", "*.json")
        );
        var selectedFile = fileChooser.showOpenDialog(primaryStage);
        if (selectedFile != null) {
            restaurantMenu.importDataFromJSON(selectedFile);
        }
    }

    private void bindingProcedure(Product product) {
        if (selectedProduct != null) {
            nameField.textProperty().unbindBidirectional(selectedProduct.nameProperty());
            Bindings.unbindBidirectional(
                    priceField.textProperty(),
                    selectedProduct.priceProperty()
            );
        }

        nameField.setText(product.getName());
        priceField.setText(String.valueOf(product.getPrice()));
        nameField.textProperty().bindBidirectional(product.nameProperty());

        Bindings.bindBidirectional(
                priceField.textProperty(),
                product.priceProperty(),
                new NumberStringConverter()
        );

        categoryField.setText(product.getCategory().toString());
        updateDetailsArea(product);
        selectedProduct = product;
    }

    private void updateDetailsArea(Product p) {
        StringBuilder sb = new StringBuilder();

        if (p instanceof Pizza pizza) {
            sb.append("Gramaj: ").append(pizza.getWeight()).append("g\n");
            sb.append("Blat: ").append(pizza.getDough()).append("\n");
            sb.append("Sos: ").append(pizza.getSauce()).append("\n");
            sb.append("Topping-uri: ").append(pizza.getCustomToppings());
        } else if (p instanceof Food food) {
            sb.append("Gramaj: ").append(food.getWeight()).append("g\n");
            sb.append("Vegetarian: ").append(food.getVegetarian() ? "Da" : "Nu");
        } else if (p instanceof Drink drink) {
            sb.append("Volum: ").append(drink.getVolume()).append("ml");
        }

        detailsArea.setText(sb.toString());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
