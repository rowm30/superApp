package com.superapp.order.api;

import com.superapp.order.client.ProductNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ── 400: client ki galti ──
    // Usne aisa product maanga jo hai hi nahi. Retry karne se kabhi
    // nahi banega, isliye 4xx dena zaroori hai.
    @ExceptionHandler(ProductNotFoundException.class)
    public ProblemDetail handleProductNotFound(ProductNotFoundException ex) {

        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Product not found");
        problem.setDetail("Product id " + ex.getProductId() + " maujood nahi hai.");

        // Extra field — client apne code mein isse use kar sakta hai
        problem.setProperty("productId", ex.getProductId());
        return problem;
    }


    // ── 503: pahunch hi nahi paaye ──
    // ResourceAccessException = connection refused, timeout, DNS fail.
    // Yaani service band hai. Retry karna SAHI hai.
    @ExceptionHandler(ResourceAccessException.class)
    public ProblemDetail handleUnreachable(ResourceAccessException ex) {

        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.SERVICE_UNAVAILABLE);
        problem.setTitle("Product service unavailable");
        problem.setDetail("Order abhi place nahi ho sakta. Thodi der baad koshish karein.");
        return problem;
    }


    // ── 502: pahunche, par wahan gadbad ──
    // Baaki sab downstream error — jaise product-service ne 500 diya.
    // 502 Bad Gateway ka matlab hi yahi hai: "maine aage poochha, uske
    // jawab mein gadbad thi". 500 dena galat hoga — galti meri nahi hai.
    @ExceptionHandler(RestClientException.class)
    public ProblemDetail handleDownstreamError(RestClientException ex) {

        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_GATEWAY);
        problem.setTitle("Downstream error");
        problem.setDetail("Product service se sahi jawab nahi mila.");

        // ⚠️ ex.getMessage() KABHI client ko mat bhejo — usme internal
        // hostname, port, aur poora URL leak hota hai. Wo sirf logs mein.
        return problem;
    }
}