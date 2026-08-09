package com.superapp.edge_gateway;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class PassportFilter implements GlobalFilter, Ordered {


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return ReactiveSecurityContextHolder.getContext()

                .map(SecurityContext::getAuthentication)
                .filter(auth -> auth instanceof JwtAuthenticationToken)
                .map(auth -> ((JwtAuthenticationToken) auth).getToken())
                .flatMap(jwt -> chain.filter(addPassport(exchange, jwt)))
                .switchIfEmpty(chain.filter(stripPassport(exchange)));
    }

    private ServerWebExchange addPassport(ServerWebExchange exchange, Jwt jwt){
        return exchange.mutate().request(r->r
                .header("X-Passport-Sub", jwt.getSubject())
                .header("X-Passport-Sid", jwt.getClaimAsString("sid"))
                .header("X-Passport-Username", jwt.getClaimAsString("preferred_username"))
        ).build();
    }

    private ServerWebExchange stripPassport(ServerWebExchange exchange){
        return exchange.mutate().request(r->r.headers(h->{
            h.remove("X-Passport");
            h.remove("X-Passport-Sid");
            h.remove("X-Passport-Username");
        })).build();
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
