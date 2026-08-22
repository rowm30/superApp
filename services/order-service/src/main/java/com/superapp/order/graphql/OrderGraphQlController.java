package com.superapp.order.graphql;

import com.superapp.order.domain.Order;
import com.superapp.order.domain.OrderItem;
import com.superapp.order.repository.OrderRepository;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.ContextValue;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// @Controller — REST ka @RestController nahi. GraphQL ka apna annotation.
@Controller
public class OrderGraphQlController {

    private final OrderRepository orderRepository;

    public OrderGraphQlController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    // @QueryMapping = schema.graphqls ke "Query" type ke field se jud raha hai.
    // Method ka naam field ke naam se match karta hai ("myOrders").
    //
    // @Transactional zaroori hai — order.getItems() LAZY hai, aur usse
    // access karna transaction ke andar hi safe hai. Interceptor ne
    // context mein buyerId daal diya tha, yahan @ContextValue se nikaal rahe hain.
    @QueryMapping
    @Transactional(readOnly = true)
    public List<OrderDto> myOrders(@ContextValue("buyerId") String buyerId) {
        return orderRepository.findByBuyerId(buyerId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @QueryMapping
    @Transactional(readOnly = true)
    public OrderDto order(@Argument Long id, @ContextValue("buyerId") String buyerId) {

        Order order = orderRepository.findById(id).orElse(null);
        if (order == null) {
            return null;
        }

        // ⚠️ OWNERSHIP CHECK — bilkul REST wale pattern jaisa.
        // Buyer A, buyer B ka order id maang ke uska data nahi le sakta,
        // chahe ID guess kar le. Passport se aayi identity yahan
        // access-control ka kaam kar rahi hai — RAG mein bhi yehi kiya tha.
        if (!order.getBuyerId().equals(buyerId)) {
            throw new AccessDeniedGraphQlException("Ye order tumhara nahi hai");
        }

        return toDto(order);
    }

    // Entity → DTO, transaction ke ANDAR hi (lazy fields yahan safe hain)
    private OrderDto toDto(Order order) {
        List<OrderItemDto> items = order.getItems().stream()
                .map(this::toDto)
                .toList();

        return new OrderDto(
                order.getId(),
                order.getStatus().name(),
                order.getTotalAmount().doubleValue(),
                order.getCreatedAt() == null ? null : order.getCreatedAt().toString(),
                items);
    }

    private OrderItemDto toDto(OrderItem item) {
        return new OrderItemDto(
                item.getId(),
                item.getProductId(),
                item.getProductName(),
                item.getUnitPrice().doubleValue(),
                item.getQuantity(),
                item.lineTotal().doubleValue());
    }
}