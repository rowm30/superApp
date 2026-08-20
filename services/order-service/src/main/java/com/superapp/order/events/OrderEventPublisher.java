package com.superapp.order.events;

import com.superapp.order.domain.Order;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class OrderEventPublisher {
    public static final String TOPIC = "order.placed.v1";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public OrderEventPublisher(KafkaTemplate<String, Object> kafkaTemplate){
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishOrderPlaced(Order order){
        OrderPlacedEvent event =  new OrderPlacedEvent(
                order.getId(),
                order.getBuyerId(),
                order.getTotalAmount(),
                order.getItems().stream()
                        .map(i -> new OrderPlacedEvent.Item(i.getProductId(), i.getQuantity()))
                        .toList(),
                Instant.now()
        );

        kafkaTemplate.send(TOPIC, String.valueOf(order.getId()), event);
    }
}
