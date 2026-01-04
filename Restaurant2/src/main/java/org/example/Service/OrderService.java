package org.example.Service;

import org.example.Entity.Order;
import org.example.Entity.OrderItem;
import org.example.Entity.Product;
import org.example.Entity.User;
import org.example.Repository.OrderRepository;

import java.util.List;

public class OrderService {
    private final OrderRepository orderRepository = new OrderRepository();
    private final OfferService offerService = new OfferService();

    public void addItemToOrder(Order order, Product product, int quantity) {
        for (OrderItem item : order.getItems()) {
            if (item.getProduct().getId().equals(product.getId())) {
                item.setQuantity(item.getQuantity() + quantity);
                return;
            }
        }
        OrderItem newItem = new OrderItem(product, quantity);
        order.addItem(newItem);
    }

    public void removeItemFromOrder(Order order, OrderItem item) {
        order.removeItem(item);
    }

    public double calculateTotal(Order order) {
        double subtotal = order.getItems().stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();
        double discount = offerService.calculateDiscount(order.getItems());
        order.setTotal(Math.max(0, subtotal - discount));
        return subtotal - discount;
    }

    public String getAppliedOffers(){
        return offerService.getActiveOffers();
    }

    public void saveOrder(Order order) {
        calculateTotal(order);
        order.setStatus("COMPLETED");
        orderRepository.save(order);
    }

    public List<Order> getOrdersHistory(User user) {
        if (user.getRole() == User.Role.ADMIN) {
            return orderRepository.findAll();
        }
        else{
            return orderRepository.findByUser(user);
        }
    }
}
