package com.superapp.order.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;



@RestController
@RequestMapping("/orders")
public class OrderController {

    @GetMapping("/ping")
    public Map<String, String> ping(
            @RequestHeader(value = "X-Passport-Username", required = false)
            String username
    ){
        return Map.of("service", "order-service", "caller", username == null ? "unknown" : username);
    }
}
