package com.superapp.order.domain;

import com.superapp.order.api.CreateOrderRequest;
import com.superapp.order.client.ProductClient;
import com.superapp.order.events.OrderEventPublisher;
import com.superapp.order.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {
    private final OrderRepository repo;
    private final ProductClient productClient;
    private final OrderEventPublisher eventPublisher;

    public OrderService(OrderRepository repo, ProductClient productClient, OrderEventPublisher eventPublisher) {
        this.repo = repo;
        this.productClient = productClient;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public Order placeOrder(CreateOrderRequest request, String buyerId, String username) {

        Order order = new Order(buyerId);

        for (CreateOrderRequest.Item item : request.items()) {

            // Daam SERVER se laa rahe hain, client se nahi
            var product = productClient.getProduct(item.productId(), buyerId, username);

            // Snapshot bana rahe hain — aaj ka naam, aaj ka daam
            OrderItem orderItem = new OrderItem(
                    product.id(),
                    product.name(),
                    product.price(),
                    item.quantity()
            );

            // helper method — dono taraf ka rishta jodta hai aur total update karta hai
            order.addItem(orderItem);
        }

        Order saved = repo.save(order);

        eventPublisher.publishOrderPlaced(saved);
        return saved;
    }
}
