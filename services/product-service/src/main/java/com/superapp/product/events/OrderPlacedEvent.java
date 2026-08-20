package com.superapp.product.events;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

// ⚠️ Ye order-service wali class ki NAKAL hai — jaan-boojh kar.
// Do raaste the:
//   1. ek shared "contracts" module banao — dono services use karein
//   2. har service apni copy rakhe
// Copy ka faayda: services ek doosre se juड़ी nahi rehtin, alag-alag
// deploy ho sakti hain. Nuksaan: contract badla toh do jagah badalna.
// Zyadatar teams copy hi rakhti hain — coupling se bachne ke liye.
public record OrderPlacedEvent(
        Long orderId,
        String buyerId,
        BigDecimal totalAmount,
        List<Item> items,
        Instant occurredAt) {

    public record Item(Long productId, Integer quantity) {
    }
}