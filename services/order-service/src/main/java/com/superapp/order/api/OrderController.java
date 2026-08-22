package com.superapp.order.api;

import com.superapp.order.domain.Order;
import com.superapp.order.domain.OrderService;
import com.superapp.order.repository.OrderRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;



@RestController
@RequestMapping("/orders")
public class OrderController {

    // Constructor mein OrderService add karo
    private final OrderService orderService;
    private final OrderRepository orderRepository;

    public OrderController(OrderService orderService, OrderRepository orderRepository) {
        this.orderService = orderService;
        this.orderRepository = orderRepository;
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

    @GetMapping("/mine")
    public List<Order> mine(@RequestHeader("X-Passport-Sub") String buyerId){
        return orderRepository.findByBuyerId(buyerId);
    }
}
