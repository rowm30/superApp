package com.superapp.order.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;

@Component
public class ProductClient {

    private final RestClient restClient;

    public ProductClient(@Value("${services.product.url}") String baseUrl) {
        // RestClient.create() ek static factory hai — spring-web mein hi hai,
        // koi autoconfiguration ki zaroorat nahi.
        // Builder wala tareeka behtar hai (observability, common config milti hai),
        // par uske liye alag module chahiye.
        this.restClient = RestClient.create(baseUrl);
    }

    public ProductResponse getProduct(Long id, String passportSub, String passportUsername) {
        return restClient.get()
                .uri("/products/{id}", id)
                .header("X-Passport-Sub", passportSub)
                .header("X-Passport-Username", passportUsername)
                .retrieve()
                .body(ProductResponse.class);
    }

    public record ProductResponse(Long id, String name, BigDecimal price, String sellerId) {
    }
}