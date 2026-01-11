package org.example.Service;

import org.example.Entity.Order;
import org.example.Entity.OrderItem;
import org.example.Entity.Product;
import org.example.Entity.User;
import org.example.Repository.OrderRepository;
import org.example.Repository.ProductRepository;

import java.util.List;

public class OrderService {
    private final OrderRepository orderRepository = new OrderRepository();
    private final OfferService offerService = new OfferService();

    public void addItemToOrder(Order order, Product product, int quantity) {
        boolean found = false;
        for (OrderItem item : order.getItems()) {
            if (item.getProduct().getId() != null
                    && product.getId() != null
                    && item.getProduct().getId().equals(product.getId())) {
                item.setQuantity(item.getQuantity() + quantity);
                found = true;
                break;
            }
        }
        if (!found) {
            OrderItem newItem = new OrderItem(product, quantity);
            order.addItem(newItem);
        }
    }

    public void removeItemFromOrder(Order order, OrderItem item) {
        order.removeItem(item);
    }

    public double calculateTotal(Order order) {
        double subtotal = 0.0;
        for (OrderItem item : order.getItems()) {
            subtotal += item.getPrice();
        }

        double discount = offerService.calculateTotalDiscount(order);
        order.setTotal(Math.max(0, subtotal - discount));
        return subtotal - discount;
    }

    public void saveOrder(Order order) {
        try{
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            System.err.println(e);
        }
        calculateTotal(order);
        order.setStatus("COMPLETED");
        orderRepository.save(order);
    }

    public List<Order> getOrdersHistory(User user) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            System.err.println(e);
        }

        if (user.getRole() == User.Role.ADMIN) {
            return orderRepository.findAll();
        }
        else{
            return orderRepository.findByUser(user);
        }
    }
}
