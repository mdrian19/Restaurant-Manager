package org.example;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.converter.NumberStringConverter;

public class RestaurantApp extends Application {
    private TextField nameField;
    private TextField priceField;
    private TextField categoryField;
    private TextArea detailsArea;

    private Product selectedProduct = null;

    @Override
    public void start(Stage primaryStage) {
        Menu menu = new Menu();

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(15));

        VBox leftPane = new VBox(8);
        Label listLabel = new Label("Products list");
        listLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        ListView<Product> products = new ListView<>();
        products.setItems(menu.getAllProductsObservable());

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

        Scene scene = new Scene(root, 1000, 650);
        primaryStage.setTitle("Restaurant \"La Andrei\"");
        primaryStage.setScene(scene);
        primaryStage.show();
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
            sb.append("Vegetarian: ").append(food.isVegetarian() ? "Da" : "Nu");
        } else if (p instanceof Drink drink) {
            sb.append("Volum: ").append(drink.getVolume()).append("ml");
        }

        detailsArea.setText(sb.toString());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
