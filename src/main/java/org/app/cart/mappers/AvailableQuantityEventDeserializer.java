package org.app.cart.mappers;



import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.kafka.common.serialization.Deserializer;
import org.app.cart.events.AvailableQuantityEvent;

import java.util.Map;
@ApplicationScoped
public class AvailableQuantityEventDeserializer implements Deserializer<AvailableQuantityEvent> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        // Configuration logic if needed
    }

    @Override
    public AvailableQuantityEvent deserialize(String topic, byte[] data) {
        try {
            return objectMapper.readValue(data, AvailableQuantityEvent.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize AvailableQuantityEvent", e);
        }
    }

    @Override
    public void close() {
        // Cleanup logic if needed
    }
}

