package org.app.cart.events;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@ApplicationScoped
@RequiredArgsConstructor
@AllArgsConstructor
public class CheckQuantityEvent {
    private Long productId;
    private int requestedQuantity;
}
