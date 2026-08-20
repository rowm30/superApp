package com.superapp.support.api;

import org.springframework.ai.chat.client.ChatClient;

import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/support")
public class ChatController {

    private final ChatClient chatClient;

    public ChatController(ChatClient.Builder builder, VectorStore vectorStore) {
        this.chatClient = builder
                .defaultSystem("You are a helpful customer support assistant for an "
                        + "ecommerce app. Answer using ONLY the provided context. "
                        + "If the context doesn't contain the answer, say you don't know "
                        + "and suggest contacting support.")

                // ⚠️ YE HI ASLI RAG HAI.
                // QuestionAnswerAdvisor har request ke saath automatically:
                //   1. user ka sawaal embed karta hai
                //   2. vectorStore mein similarity search karta hai
                //   3. top matching chunks nikalta hai
                //   4. unhe prompt mein "context" ke roop mein daal deta hai
                // Hume ye sab manually likhna nahi pada — Spring AI's
                // "Advisor" pattern is poore flow ko wrap kar deta hai.
                .defaultAdvisors(QuestionAnswerAdvisor.builder(vectorStore).build())
                .build();
    }

    @PostMapping("/chat")
    public Map<String, String> chat(@RequestBody Map<String, String> request) {

        String question = request.get("question");

        String answer = chatClient.prompt()
                .user(question)
                .call()
                .content();

        return Map.of("answer", answer);
    }
}