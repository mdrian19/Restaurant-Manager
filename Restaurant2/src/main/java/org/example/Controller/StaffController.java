package org.example.Controller;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.Entity.*;
import org.example.Service.OfferService;
import org.example.Service.OrderService;
import org.example.Service.ProductService;

import java.time.format.DateTimeFormatter;
import java.util.List;
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

    @FXML private ProgressIndicator loadingSpinner;

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

        var visibleProducts = productService.getAllProducts().stream()
                .filter(p -> !p.getName().equalsIgnoreCase("Bere gratis"))
                .collect(Collectors.toList());
        menuTable.setItems(FXCollections.observableArrayList(visibleProducts));

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

        loadingSpinner.setVisible(true);
        orderTable.setDisable(true);

        Task<Void> saveTask = new Task<>() {
            @Override
            protected Void call() {
                currentOrder.setUser(currentUser);
                orderService.saveOrder(currentOrder);
                return null;
            }
        };

        saveTask.setOnSucceeded(event -> {
            loadingSpinner.setVisible(false);
            orderTable.setDisable(false);
            new Alert(Alert.AlertType.INFORMATION, "Order finalized successfully!").show();

            currentOrder = null;
            orderTable.getItems().clear();
            totalLabel.setText("0.00 RON");
            tablesList.getSelectionModel().clearSelection();
            handleRefreshHistory();
        });

        saveTask.setOnFailed(event -> {
            loadingSpinner.setVisible(false);
            orderTable.setDisable(false);
            new Alert(Alert.AlertType.ERROR, "Failed to finalize order.").show();
        });

        new Thread(saveTask).start();
    }

    @FXML
    private void handleRefreshHistory() {
        Task<List<Order>> loadTask = new Task<>() {
            @Override
            protected List<Order> call() {
                return orderService.getOrdersHistory(currentUser);
            }
        };

        loadTask.setOnSucceeded(event -> {
            ordersHistoryTable.setItems(FXCollections.observableArrayList(loadTask.getValue()));
            loadingSpinner.setVisible(false);
        });

        loadTask.setOnFailed(event -> {
            loadingSpinner.setVisible(false);
            new Alert(Alert.AlertType.ERROR, "Failed to load order history.").show();
        });

        loadingSpinner.setVisible(true);
        new Thread(loadTask).start();
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
        if (offerService.isPartyPackActive()) sb.append("Party Pack active\n");
        if (offerService.isMealDealActive()) sb.append("Meal Deal active\n");
        if (offerService.isHappyHourActive()) sb.append("Happy Hour active\n");

        if (sb.isEmpty()) offersLabel.setText("No active offers.");
        else offersLabel.setText(sb.toString());
    }
}

