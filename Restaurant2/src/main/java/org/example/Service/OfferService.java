package org.example.Service;

import org.example.Entity.*;
import org.example.Repository.OfferRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OfferService {
    private final OfferRepository offerRepository = new OfferRepository();

    private static final String HAPPY_HOUR = "HAPPY_HOUR_DRINKS";
    private static final String MEAL_DEAL = "MEAL_DEAL";
    private static final String PARTY_PACK = "PARTY_PACK";

    public OfferService(){
        seedOffers();
    }

    private void seedOffers() {
        if (offerRepository.findByName(HAPPY_HOUR) == null) offerRepository.save(new Offer(HAPPY_HOUR, false));
        if (offerRepository.findByName(MEAL_DEAL) == null) offerRepository.save(new Offer(MEAL_DEAL, false));
        if (offerRepository.findByName(PARTY_PACK) == null) offerRepository.save(new Offer(PARTY_PACK, false));
    }


    public boolean isHappyHourActive() {
        Offer o = offerRepository.findByName(HAPPY_HOUR);
        return o != null && o.isActive();
    }
    public void setHappyHourActive(boolean active) {
        offerRepository.save(new Offer(HAPPY_HOUR, active));
    }

    public boolean isMealDealActive() {
        Offer o = offerRepository.findByName(MEAL_DEAL);
        return o != null && o.isActive();
    }
    public void setMealDealActive(boolean active) {
        offerRepository.save(new Offer(MEAL_DEAL, active));
    }

    public boolean isPartyPackActive() {
        Offer o = offerRepository.findByName(PARTY_PACK);
        return o != null && o.isActive();
    }
    public void setPartyPackActive(boolean active) {
        offerRepository.save(new Offer(PARTY_PACK, active));
    }

    private boolean isActive (String name){
        Offer offer = offerRepository.findByName(name);
        return offer != null && offer.isActive();
    }

    private void setActive(String name, boolean active){
        offerRepository.save(new Offer(name, active));
    }


    public double calculateTotalDiscount(Order order) {
        double discount = 0.0;
        if (isPartyPackActive()) discount += calculatePartyPack(order);
        if (isMealDealActive()) discount += calculateMealDeal(order);
        if (isHappyHourActive()) discount += calculateHappyHour(order);
        return discount;
    }

    private double calculatePartyPack(Order order){
        List<Double> pizzaPrices = getPricesForCategory(order, "MAIN_COURSE", "Pizza");
        Collections.sort(pizzaPrices);

        int freePizzas = pizzaPrices.size() / 4;
        double discount = 0.0;
        for (int i = 0; i < freePizzas; i++) {
            discount += pizzaPrices.get(i);
        }
        return discount;
    }

    private double calculateMealDeal(Order order){
        long pizzaCount = order.getItems().stream()
                .filter(item -> isPizza(item.getProduct()))
                .mapToLong(OrderItem::getQuantity)
                .sum();

        List<Double> dessertPrices = getPricesForCategory(order, "DESSERT", null);
        Collections.sort(dessertPrices);

        double discount = 0.0;
        long pairs = Math.min(pizzaCount, dessertPrices.size());
        for (int i = 0; i < pairs; i++) {
            discount += dessertPrices.get(i) * 0.25;
        }
        return discount;
    }

    private double calculateHappyHour(Order order){
        List<Double> drinkPrices = getPricesForCategory(order, "ALCOHOLIC_DRINK", null);
        Collections.sort(drinkPrices);

        double discount = 0.0;
        int discountedCount = drinkPrices.size() / 2;
        for (int i = 0; i < discountedCount; i++) {
            discount += drinkPrices.get(i) * 0.5;
        }
        return discount;
    }

    private List<Double> getPricesForCategory(Order order, String category, String nameFilter){
        List<Double> prices = new ArrayList<>();
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            boolean matchesCategory = product.getCategory().name().equals(category);
            boolean matchesName = nameFilter == null || product.getName().toLowerCase().contains(nameFilter.toLowerCase());

            if  (matchesCategory && matchesName) {
                for (int i = 0; i < item.getQuantity(); i++) {
                    prices.add(product.getPrice());
                }
            }
        }
        return prices;
    }

    private boolean isPizza(Product p) {
        return p instanceof Pizza || (p.getCategory().name().equals("MAIN_COURSE") && p.getName().toLowerCase().contains("pizza"));
    }
}