package com.superapp.support.api;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/support")
public class ChatController {

    // ChatClient = Spring AI ka main entry point.
    // Provider-agnostic hai — Anthropic se OpenAI pe switch karo toh
    // ye code bilkul nahi badlega, sirf dependency aur properties badlengi.
    private final ChatClient chatClient;

    // Spring ne ChatClient.Builder ka bean khud diya (starter se aata hai).
    // Hum usse apna configured client banate hain.
    public ChatController(ChatClient.Builder builder) {
        this.chatClient = builder
                // System prompt = model ko uska role batana.
                // Har request ke saath apne aap jayega.
                .defaultSystem("You are a helpful customer support assistant for an "
                        + "ecommerce app. Keep answers short and factual.")
                .build();
    }

    @PostMapping("/chat")
    public Map<String, String> chat(@RequestBody Map<String, String> request) {

        String question = request.get("question");

        // Fluent API — Spring AI ka core pattern:
        //   prompt()   → naya prompt shuru karo
        //   user(...)  → user ka message
        //   call()     → bhejo aur jawab ka intezaar karo (blocking)
        //   content()  → jawab ka text nikalo
        String answer = chatClient.prompt()
                .user(question)
                .call()
                .content();

        return Map.of("answer", answer);
    }
}