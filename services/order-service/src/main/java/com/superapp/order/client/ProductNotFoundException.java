package com.superapp.order.client;

public class ProductNotFoundException extends RuntimeException {
    private final Long productId;

    public ProductNotFoundException(Long productId){
        super("Product nahi mila: " + productId);
        this.productId= productId;
    }

    public Long getProductId(){
        return productId;
    }
}
