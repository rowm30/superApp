package com.superapp.order.graphql;

public class AccessDeniedGraphQlException extends RuntimeException {
    public AccessDeniedGraphQlException(String message) {
        super(message);
    }
}