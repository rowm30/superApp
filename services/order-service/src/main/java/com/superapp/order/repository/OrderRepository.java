package com.superapp.order.repository;

import com.superapp.order.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // "mere orders" — SELECT * FROM orders WHERE buyer_id = ?
    List<Order> findByBuyerId(String buyerId);
}