package com.example.service;

import com.example.domain.Order;
import com.example.messaging.OrderProducer;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Singleton
public class OrderService {
    public List<Order> orders = new ArrayList<>();

    private final OrderProducer orderProducer;

    // Now we can inject our OrderProducer into our OrderService.
    public OrderService(OrderProducer orderProducer) {
        this.orderProducer = orderProducer;
    }

    public Order getOrderById(Long id) {
        return orders.stream().filter(it -> it.getId().equals(id)).findFirst().orElse(null);
    }

    public List<Order> listOrders() {
        return orders;
    }

    public void updateOrder(Order order) {
        Order existingOrder = getOrderById(order.getId());
        int i = orders.indexOf(existingOrder);
        orders.set(i, order);
    }

    public Order newOrder(Order order) {
        order.setId((long) orders.size());
        this.orders.add(order);
        // Now we can inject our OrderProducer into our OrderService.
        orderProducer.sendMessage(UUID.randomUUID().toString(), order.toString());
        return order;
    }
}