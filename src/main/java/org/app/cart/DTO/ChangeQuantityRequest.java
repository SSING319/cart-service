package org.app.cart.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ChangeQuantityRequest {
    private Long userId;
    private Long cartId;
    private Long productId;
    private int quantity;
}
