package com.superapp.order.domain;

import com.superapp.order.api.CreateOrderRequest;
import com.superapp.order.client.ProductClient;
import com.superapp.order.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {
    private final OrderRepository repo;
    private final ProductClient productClient;

    public OrderService(OrderRepository repo, ProductClient productClient) {
        this.repo = repo;
        this.productClient = productClient;
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

        return repo.save(order);
    }
}
