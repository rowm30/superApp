package com.superapp.support.api;

import com.superapp.support.tools.OrderLookupTool;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/support")
public class ChatController {

    // ⚠️ ChatClient yahan PEHLE se nahi banate — filter aur buyerId
    // dono HAR REQUEST ke user pe depend karte hain. Isliye Builder aur
    // dependencies field mein rakhte hain, ChatClient har request mein banega.
    private final ChatClient.Builder chatClientBuilder;
    private final VectorStore vectorStore;
    private final OrderLookupTool orderLookupTool;

    public ChatController(ChatClient.Builder chatClientBuilder,
                          VectorStore vectorStore,
                          OrderLookupTool orderLookupTool) {
        this.chatClientBuilder = chatClientBuilder;
        this.vectorStore = vectorStore;
        this.orderLookupTool = orderLookupTool;
    }

    @PostMapping("/chat")
    public Map<String, String> chat(
            @RequestBody Map<String, String> request,

            // Gateway se aane wala passport header — "kaun poochh raha hai"
            @RequestHeader(value = "X-Passport-Sub", required = false) String buyerId) {

        String question = request.get("question");

        // ── RAG FILTER ──
        // Sirf public docs. Per-buyer data aane par yahan
        // "OR owner == buyerId" bhi jodenge.
        FilterExpressionBuilder b = new FilterExpressionBuilder();
        Filter.Expression filter = b.eq("visibility", "public").build();

        var qaAdvisor = QuestionAnswerAdvisor.builder(vectorStore)
                .searchRequest(SearchRequest.builder()
                        .filterExpression(filter)
                        .topK(4)
                        .build())
                .build();

        // ── CHAT CLIENT — is request ke liye, RAG + tool dono ke saath ──
        String answer = chatClientBuilder
                .defaultSystem("You are a helpful customer support assistant for an "
                        + "ecommerce app. Answer using the provided context and tools. "
                        + "If asked about order status, use the order lookup tool. "
                        + "The current buyer's id is: " + buyerId)
                .build()
                .prompt()
                .advisors(qaAdvisor)

                // Model khud decide karega: tool chalana hai ya nahi
                .tools(orderLookupTool)

                .user(question)
                .call()
                .content();

        return Map.of("answer", answer, "askedBy", buyerId == null ? "anonymous" : buyerId);
    }
}