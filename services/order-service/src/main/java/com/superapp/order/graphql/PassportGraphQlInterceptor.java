package com.superapp.order.graphql;

import org.springframework.graphql.server.WebGraphQlInterceptor;
import org.springframework.graphql.server.WebGraphQlRequest;
import org.springframework.graphql.server.WebGraphQlResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class PassportGraphQlInterceptor implements WebGraphQlInterceptor {

    @Override
    public Mono<WebGraphQlResponse> intercept(WebGraphQlRequest request, Chain chain) {

        String buyerId = request.getHeaders().getFirst("X-Passport-Sub");

        // ⚠️ FIX: GraphQLContext.put() null accept nahi karta — crash hota hai.
        // Bina token wale (ya gateway se na aane wale) requests ke liye
        // ek placeholder value daal rahe hain, taaki context build ho jaye.
        // Resolver ye check karega ki "anonymous" hai toh authorization fail karega.
        String contextValue = buyerId == null ? "anonymous" : buyerId;

        request.configureExecutionInput((input, builder) ->
                builder.graphQLContext(ctx -> ctx.put("buyerId", contextValue)).build());

        return chain.next(request);
    }
}