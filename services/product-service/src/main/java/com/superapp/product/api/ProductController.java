package com.superapp.product.api;


import com.superapp.product.domain.Product;
import com.superapp.product.repository.ProductRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductRepository repo;

    public ProductController(ProductRepository repo){
        this.repo = repo;
    }

    @PostMapping
    public Product create(
            @RequestBody CreateProductRequest request,
            @RequestHeader("X-Passport-Sub") String sellerId
    ){
        Product product = new Product(request.name(), request.price(), sellerId);

        return repo.save(product);
    }

    @GetMapping("/mine")
    public List<Product> mine(@RequestHeader("X-Passport-Sub") String sellerId){
        return repo.findBySellerId(sellerId);
    }

    @GetMapping
    public List<Product> all(){
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> one(@PathVariable Long id){
        return repo.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/ping")
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
