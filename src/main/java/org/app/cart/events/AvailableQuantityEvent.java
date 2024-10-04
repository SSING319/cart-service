package org.app.cart.events;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.Data;

@Data
@ApplicationScoped
public class AvailableQuantityEvent {
    private Long productId;
    private boolean isAvailable;
    private int inStock;
}
