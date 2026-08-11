package com.superapp.order.api;

import com.superapp.order.domain.Order;
import com.superapp.order.domain.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;



@RestController
@RequestMapping("/orders")
public class OrderController {

    // Constructor mein OrderService add karo
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // POST /orders
    @PostMapping
    public Order create(
            @RequestBody CreateOrderRequest request,
            @RequestHeader("X-Passport-Sub") String buyerId,
            @RequestHeader("X-Passport-Username") String username) {

        return orderService.placeOrder(request, buyerId, username);
    }

    @GetMapping("/ping")
    public Map<String, String> ping(
            @RequestHeader(value = "X-Passport-Username", required = false)
            String username
    ){
        return Map.of("service", "order-service", "caller", username == null ? "unknown" : username);
    }
}
