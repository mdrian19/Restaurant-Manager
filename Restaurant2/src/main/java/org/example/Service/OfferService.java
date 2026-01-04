package org.example.Service;

import org.example.Entity.Drink;
import org.example.Entity.OrderItem;
import org.example.Entity.Pizza;
import org.example.Entity.Product;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class OfferService {
    private static boolean happyHourActive = false;
    private static boolean valentinesDayActive = false;
    private static boolean freeBeerActive = false;

    public static boolean isHappyHourActive() {
        return happyHourActive;
    }

    public static void setHappyHourActive(boolean happyHourActive) {
        OfferService.happyHourActive = happyHourActive;
    }

    public static boolean isValentinesDayActive() {
        return valentinesDayActive;
    }

    public static void setValentinesDayActive(boolean valentinesDayActive) {
        OfferService.valentinesDayActive = valentinesDayActive;
    }

    public static boolean isFreeBeerActive() {
        return freeBeerActive;
    }

    public static void setFreeBeerActive(boolean freeBeerActive) {
        OfferService.freeBeerActive = freeBeerActive;
    }

    public double calculateDiscount(List<OrderItem> items){
        double totalDiscount = 0.0;

        if (happyHourActive){
            totalDiscount += calculateHappyHourDiscount(items);
        }
        if (valentinesDayActive){
            totalDiscount += calculateValentinesDayDiscount(items);
        }
        if (freeBeerActive){
            totalDiscount += calculateFreeBeerDiscount(items);
        }
        return totalDiscount;
    }

    private double calculateHappyHourDiscount(List<OrderItem> items){
        double discount = 0.0;
        for (OrderItem item : items) {
            Product product = item.getProduct();
            if (product instanceof Drink && Objects.equals(product.getCategory().toString(), "ALCOHOLIC_DRINK")) {
                discount += product.getPrice() * item.getQuantity() * 0.20;
            }
        }
        return discount;
    }

    private double calculateValentinesDayDiscount(List<OrderItem> items){
        double discount = 0.0;
        for (OrderItem item : items) {
            Product product = item.getProduct();
            discount += product.getPrice() * item.getQuantity() * 0.10;
        }
        return discount;
    }

    private double calculateFreeBeerDiscount(List<OrderItem> items) {
        double discount = 0.0;
        Product freeBeer = new Drink("Free Beer", 0.0, Product.Category.ALCOHOLIC_DRINK, 500);
        for (OrderItem item : items){
            if (item.getProduct() instanceof Pizza){
                items.add(new OrderItem(freeBeer, item.getQuantity()));
            }
        }
        return 0.0;
    }

    public String getActiveOffers(){
        StringBuilder offers = new StringBuilder("Active Offers: ");
        if (happyHourActive){
            offers.append("[Happy Hour: 20% off on all alcoholic drinks] ");
        }
        if (valentinesDayActive){
            offers.append("[Valentine's Day: 10% off on all items] ");
        }
        if (freeBeerActive){
            offers.append("[Free Beer with every Pizza ordered] ");
        }
        if (offers.toString().equals("Active Offers: ")){
            offers.append("None");
        }
        return offers.toString().trim();
    }
}
