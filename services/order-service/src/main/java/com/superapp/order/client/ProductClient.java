package com.superapp.order.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;

@Component
public class ProductClient {

    private final RestClient restClient;

    public ProductClient(@Value("${services.product.url}") String baseUrl) {
        this.restClient = RestClient.create(baseUrl);
    }

    public ProductResponse getProduct(Long id, String passportSub, String passportUsername) {
        return restClient.get()
                .uri("/products/{id}", id)
                .header("X-Passport-Sub", passportSub)
                .header("X-Passport-Username", passportUsername)
                .retrieve()

                // ── NAYA ──
                // onStatus = "agar jawab ka status ye ho, toh ye karo".
                // Do argument: pehla condition, doosra kya karna hai.
                // Yahan 404 ko apni exception mein badal rahe hain.
                .onStatus(status -> status.value() == 404, (request, response) -> {
                    throw new ProductNotFoundException(id);
                })

                .body(ProductResponse.class);
    }

    public record ProductResponse(Long id, String name, BigDecimal price, String sellerId) {
    }
}