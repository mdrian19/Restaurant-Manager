package org.example.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.Entity.*;
import org.example.Service.OrderService;
import org.example.Service.ProductService;

public class StaffController {
    @FXML
    private ListView<String> tablesList;

    @FXML
    private TableView<Product> menuTable;

    @FXML
    private TableView<OrderItem> orderTable;

    @FXML
    private Label totalLabel;

    @FXML
    private Label offersLabel;

    private final ProductService productService = new ProductService();
    private final OrderService orderService = new OrderService();

    private Order currentOrder;
    private User currentUser;

    public void initializeData(User user){
        this.currentUser = user;
        menuTable.setItems(FXCollections.observableArrayList(productService.getAllProducts()));
    }

    @FXML
    public void initialize(){
        tablesList.setItems(FXCollections.observableArrayList(
                "Table 1", "Table 2", "Table 3", "Table 4", "Table 5"
        ));
        tablesList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                loadOrderForTable(newValue);
            }
        });
    }

    @FXML
    public void handleAddToOrder(){
        Product selectedProduct = menuTable.getSelectionModel().getSelectedItem();
        if (selectedProduct != null && currentOrder != null) {
            orderService.addItemToOrder(currentOrder, selectedProduct, 1);
            refreshOrderTable();
        }
    }

    @FXML
    private void handleRemoveItem(){
        OrderItem selectedItem = orderTable.getSelectionModel().getSelectedItem();
        if (selectedItem != null && currentOrder != null) {
            orderService.removeItemFromOrder(currentOrder, selectedItem);
            refreshOrderTable();
        }
    }

    @FXML
    private void handleFinalizeOrder(){
        if (currentOrder == null) return;
        try{
            orderService.saveOrder(currentOrder);
            new Alert(Alert.AlertType.INFORMATION, "Order finalized successfully.").show();
            currentOrder = null;
            orderTable.getItems().clear();
        } catch (Exception e){
            new Alert(Alert.AlertType.ERROR, "Error finalizing order.").show();
        }
    }

    private void loadOrderForTable(String tableName){
        this.currentOrder = new Order();
        refreshOrderTable();
    }

    private void refreshOrderTable(){
        if (currentOrder == null) return;

        orderTable.setItems(FXCollections.observableArrayList(currentOrder.getItems()));
        double total = orderService.calculateTotal(currentOrder);
        totalLabel.setText(String.format("%.2f RON", total));
        offersLabel.setText(orderService.getAppliedOffers());
    }
}

