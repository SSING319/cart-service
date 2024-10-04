package org.app.cart.mappers;



import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.kafka.common.serialization.Serializer;
import org.app.cart.events.CheckQuantityEvent;

import java.util.Map;
@ApplicationScoped
public class CheckQuantityEventSerializer implements Serializer<CheckQuantityEvent> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        // Configuration logic if needed
    }

    @Override
    public byte[] serialize(String topic, CheckQuantityEvent data) {
        try {
            return objectMapper.writeValueAsBytes(data);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize CheckQuantityEvent", e);
        }
    }

    @Override
    public void close() {
        // Cleanup logic if needed
    }
}
