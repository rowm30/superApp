package com.superapp.support.config;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

// CommandLineRunner = "app poori tarah start hone ke baad ye method chalao".
// Isse hum boot hote hi apne docs load kar dete hain.
@Component
public class DocumentIngestionRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DocumentIngestionRunner.class);

    private final VectorStore vectorStore;

    // classpath: prefix = "resources folder mein dhoondho"
    @Value("classpath:docs/return-policy.md")
    private Resource policyDoc;

    public DocumentIngestionRunner(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @Override
    public void run(String... args) throws Exception {

        // ⚠️ Idempotency check — bina iske har restart pe docs DOBARA
        // insert ho jaate, aur duplicate chunks retrieval quality kharab karte.
        // similaritySearch se poochh rahe hain: "kuch already hai kya?"
        List<Document> existing = vectorStore.similaritySearch(
                org.springframework.ai.vectorstore.SearchRequest.builder()
                        .query("return policy")
                        .topK(1)
                        .build());

        if (!existing.isEmpty()) {
            log.info("Docs already ingested, skip kar rahe hain");
            return;
        }

        log.info("Docs ingest kar rahe hain...");

        // 1. Poora document ek TextReader se padho
        TextReader reader = new TextReader(policyDoc);
        List<Document> documents = reader.get();

        // 2. Chunk karo. Poora document ek saath embed karna 2 problem deta hai:
        //    - LLM context limit
        //    - Precision: chhota chunk = zyada targeted retrieval
        // TokenTextSplitter default ~800 tokens/chunk, thoda overlap ke saath
        // (taaki chunk boundary pe koi sentence beech mein na kate).
        TokenTextSplitter splitter = new TokenTextSplitter();
        List<Document> chunks = splitter.apply(documents);

        // 3. VectorStore mein daalo. Spring AI khud:
        //    - ONNX model se har chunk ka embedding banata hai
        //    - Postgres mein text + embedding + metadata save karta hai
        vectorStore.add(chunks);

        log.info("{} chunks ingest ho gaye", chunks.size());
    }
}