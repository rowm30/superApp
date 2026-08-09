package com.superapp.product;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ProductController {

    @GetMapping("/products/ping")
    public Map<String, String> ping(
            @RequestHeader(value = "X-Passport-Sub", required = false) String sub,
            @RequestHeader(value= "X-Passport-Username", required = false) String username
    )
    {
        return Map.of(
                "service", "product-service",
                "caller", username == null ? "unknown" : username,
                "callerId", sub == null ? "unknown" : sub
        );
    }
}
