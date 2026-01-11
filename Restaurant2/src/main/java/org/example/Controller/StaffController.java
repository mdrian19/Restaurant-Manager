package org.example.Controller;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.Entity.*;
import org.example.Service.OfferService;
import org.example.Service.OrderService;
import org.example.Service.ProductService;

import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

public class StaffController {
    @FXML private ListView<String> tablesList;

    @FXML private TableView<Product> menuTable;
    @FXML private TableColumn<Product, String> productName;
    @FXML private TableColumn<Product, Double> productPrice;
    @FXML private TableColumn<Product, String> productCategory;

    @FXML private TableView<OrderItem> orderTable;
    @FXML private TableColumn<OrderItem, String> orderItemName;
    @FXML private TableColumn<OrderItem, Double> orderItemPrice;
    @FXML private TableColumn<OrderItem, Integer> orderItemQuantity;

    @FXML private Label totalLabel;
    @FXML private Label offersLabel;

    @FXML private TableView<Order> ordersHistoryTable;
    @FXML private TableColumn<Order, Long> orderId;
    @FXML private TableColumn<Order, String> orderDate;
    @FXML private TableColumn<Order, Double> orderTotal;
    @FXML private TableColumn<Order, String> orderDetails;

    private final ProductService productService = new ProductService();
    private final OrderService orderService = new OrderService();
    private final OfferService offerService = new OfferService();

    private Order currentOrder;
    private User currentUser;

    public void initializeData(User user){
        this.currentUser = user;
        if (currentUser != null) refreshOrdersHistoryTable();

    }

    @FXML
    public void initialize(){
        tablesList.setItems(FXCollections.observableArrayList(
                "Table 1", "Table 2", "Table 3", "Table 4", "Table 5", "Table 6", "Table 7"
        ));
        tablesList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                loadOrderForTable(newValue);
            }
        });

        productName.setCellValueFactory(cell -> cell.getValue().nameProperty());
        productPrice.setCellValueFactory(cell -> cell.getValue().priceProperty().asObject());
        productCategory.setCellValueFactory(cell -> cell.getValue().categoryStringProperty());
        menuTable.setItems(FXCollections.observableArrayList(productService.getAllProducts()));

        orderItemName.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getProduct().getName()));
        orderItemPrice.setCellValueFactory(cell -> new SimpleDoubleProperty(cell.getValue().getPrice()).asObject());
        orderItemQuantity.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getQuantity()).asObject());

        orderId.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getId()));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        orderDate.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getDate().format(formatter)));
        orderTotal.setCellValueFactory(cell -> cell.getValue().totalProperty().asObject());
        orderDetails.setCellValueFactory(cell -> {
            String details = cell.getValue().getItems().stream()
                    .map(item -> item.getProduct().getName() + " x" + item.getQuantity())
                    .collect(Collectors.joining(", "));
            return new SimpleStringProperty(details);
        });

        updateActiveOffersLabel();
    }

    @FXML
    public void handleAddToOrder(){
        Product selectedProduct = menuTable.getSelectionModel().getSelectedItem();
        if (currentOrder == null){
            new Alert(Alert.AlertType.WARNING, "Please select a table first.").show();
            return;
        }
        if (selectedProduct != null) {
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
        if (currentOrder == null || currentOrder.getItems().isEmpty()){
            new Alert(Alert.AlertType.WARNING, "No items in the order to finalize.").show();
            return;
        }

        try{
            currentOrder.setUser(currentUser);
            orderService.saveOrder(currentOrder);
            new Alert(Alert.AlertType.INFORMATION, "Order finalized successfully.").show();

            currentOrder = null;
            orderTable.getItems().clear();
            totalLabel.setText("0.00 RON");
            tablesList.getSelectionModel().clearSelection();
            refreshOrdersHistoryTable();
        } catch (Exception e){
            new Alert(Alert.AlertType.ERROR, "Error finalizing order.").show();
        }
    }

    @FXML
    private void handleRefreshHistory() {
        refreshOrdersHistoryTable();
    }

    private void loadOrderForTable(String tableName){
        this.currentOrder = new Order();
        refreshOrderTable();
    }

    private void refreshOrderTable(){
        if (currentOrder == null) return;
        orderTable.setItems(FXCollections.observableArrayList(currentOrder.getItems()));
        orderTable.refresh();
        double total = orderService.calculateTotal(currentOrder);
        totalLabel.setText(String.format("%.2f RON", total));
        // offersLabel.setText(orderService.getAppliedOffers());
    }

    private void refreshOrdersHistoryTable(){
        if (currentUser == null) return;
        ordersHistoryTable.setItems(FXCollections.observableArrayList(
                orderService.getOrdersHistory(currentUser)
        ));
    }

    private void updateActiveOffersLabel() {
        StringBuilder sb = new StringBuilder();
        if (offerService.isHappyHourActive()) sb.append("Happy Hour (-20% bauturi alcoolice)\n");
        if (offerService.isValentinesDayActive()) sb.append("Valentine's day (-10% tot)\n");
        if (offerService.isFreeBeerActive()) sb.append("Bere gratis la Pizza\n");

        if (sb.isEmpty()) offersLabel.setText("No active offers.");
        else offersLabel.setText(sb.toString());
    }
}

