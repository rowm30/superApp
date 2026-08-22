package com.superapp.order.graphql;

import java.util.List;

// ⚠️ JPA entity nahi bhej rahe seedha — ye DTO hai.
// Wajah: Order.items LAZY hai, GraphQL execution transaction ke bahar
// ho sakta hai, aur lazy field access karte hi
// LazyInitializationException aata. DTO mein hum saari values
// TRANSACTION KE ANDAR hi nikaal lete hain (neeche controller mein).
public record OrderDto(
        Long id,
        String status,
        double totalAmount,
        String createdAt,
        List<OrderItemDto> items) {
}