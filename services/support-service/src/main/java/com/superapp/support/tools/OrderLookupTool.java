package com.superapp.support.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Component
public class OrderLookupTool {
    private final RestClient restClient = RestClient.create("http://localhost:8084");

    @Tool(description = "Get the current status, items, and total amount of the "
            + "authenticated buyer's most recent orders. Use this when the user "
            + "asks about their order status, delivery, or what they bought.")
    public List<Map<String,Object>> getMyRecentOrders(
            @ToolParam(description = "The buyer's unique identifier (passport subject id)")
            String buyerId){

        return restClient.get()
                .uri("/orders/mine")
                .header("X-Passport-Sub", buyerId)
                .retrieve()
                .body(List.class);
    }

}
