package com.superapp.product.api;

import java.math.BigDecimal;

public record CreateProductRequest(String name, BigDecimal price) {
}
