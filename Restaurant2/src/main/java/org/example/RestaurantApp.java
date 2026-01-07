package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.Stage;
import org.example.Entity.*;
import org.example.Service.ProductService;
import org.example.Service.UserService;

public class RestaurantApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{
        ProductService productService = new ProductService();
        productService.seedDatabaseIfEmpty();

        UserService userService = new UserService();
        userService.seedAdminUser();

        try{
            Parent root = FXMLLoader.load(getClass().getResource("/org/example/View/Guest.fxml"));
            Scene scene = new Scene(root);
            primaryStage.setTitle("La Andrei");
            primaryStage.setScene(scene);
            primaryStage.show();
        }
        catch (Exception e){
            System.out.println("Error loading initial interface: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
