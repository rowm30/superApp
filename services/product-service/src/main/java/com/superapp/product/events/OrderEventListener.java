package com.superapp.product.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderEventListener {

    private static final Logger log = LoggerFactory.getLogger(OrderEventListener.class);

    // @KafkaListener = Spring khud ek background thread chalayega
    // jo is topic ko sunta rahega aur har message pe ye method call karega.
    @KafkaListener(topics = "order.placed.v1")
    public void onOrderPlaced(OrderPlacedEvent event) {

        if(event == null){
            log.warn("Kharab message mila, chhod rahe hai");
            return;
        }

        log.info("Event mila - order {} buyer {} item{}",
                event.orderId(), event.buyerId(), event.items().size());

        for(OrderPlacedEvent.Item item : event.items()){
            log.info(" product {} ka stock {} ghatana hai", item.productId(), item.quantity());
        }
    }
}