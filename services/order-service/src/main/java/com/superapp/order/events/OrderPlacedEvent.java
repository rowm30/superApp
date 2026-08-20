package com.superapp.order.events;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

// Ye event ka CONTRACT hai — doosri services isi shape pe bharosa karengi.
// ⚠️ Isme field HATANA ya naam badalna doosri services ko toḍ dega.
//    Naya field jodna safe hai. Bada badlaav chahiye toh v2 topic banao.
public record OrderPlacedEvent(
        Long orderId,
        String buyerId,
        BigDecimal totalAmount,
        List<Item> items,
        Instant occurredAt) {

    // Consumer ko yahi chahiye — kaunsa product, kitna stock ghatana hai
    public record Item(Long productId, Integer quantity) {
    }
}