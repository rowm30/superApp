package com.superapp.order.graphql;

public record OrderItemDto(
        Long id,
        Long productId,
        String productName,
        double unitPrice,
        int quantity,
        double lineTotal) {
}