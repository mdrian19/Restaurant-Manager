package org.example.Service;

import org.example.Entity.*;
import org.example.Repository.OfferRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class OfferService {
    private final OfferRepository offerRepository = new OfferRepository();

    private static final String HAPPY_HOUR = "HAPPY_HOUR";
    private static final String VALENTINES_DAY = "VALENTINES_DAY";
    private static final String FREE_BEER = "FREE_BEER";

    public OfferService(){
        if (offerRepository.findByName(HAPPY_HOUR) == null) {
            offerRepository.save(new Offer(HAPPY_HOUR, false));
        }
        if (offerRepository.findByName(VALENTINES_DAY) == null) {
            offerRepository.save(new Offer(VALENTINES_DAY, false));
        }
        if (offerRepository.findByName(FREE_BEER) == null) {
            offerRepository.save(new Offer(FREE_BEER, false));
        }
    }

    public boolean isHappyHourActive() {
        Offer offer = offerRepository.findByName(HAPPY_HOUR);
        return offer != null && offer.isActive();
    }

    public void setHappyHourActive(boolean active) {
        Offer offer = new Offer(HAPPY_HOUR, active);
        offerRepository.save(offer);
    }

    public boolean isValentinesDayActive() {
        Offer offer = offerRepository.findByName(VALENTINES_DAY);
        return offer != null && offer.isActive();
    }

    public void setValentinesDayActive(boolean active) {
        Offer offer = new Offer(VALENTINES_DAY, active);
        offerRepository.save(offer);
    }

    public boolean isFreeBeerActive() {
        Offer offer = offerRepository.findByName(FREE_BEER);
        return offer != null && offer.isActive();
    }

    public void setFreeBeerActive(boolean active) {
        Offer offer = new Offer(FREE_BEER, active);
        offerRepository.save(offer);
    }

    public double calculateDiscount(List<OrderItem> items){
        double totalDiscount = 0.0;

        if (isHappyHourActive()){
            totalDiscount += calculateHappyHourDiscount(items);
        }
        if (isValentinesDayActive()){
            totalDiscount += calculateValentinesDayDiscount(items);
        }
        if (isFreeBeerActive()){
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
        if (isHappyHourActive()){
            offers.append("[Happy Hour: 20% off on all alcoholic drinks] ");
        }
        if (isValentinesDayActive()){
            offers.append("[Valentine's Day: 10% off on all items] ");
        }
        if (isFreeBeerActive()){
            offers.append("[Free Beer with every Pizza ordered] ");
        }
        if (offers.toString().equals("Active Offers: ")){
            offers.append("None");
        }
        return offers.toString().trim();
    }
}
