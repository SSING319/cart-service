package org.app.cart.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class AddToCartRequest {
    private Long userId;
    private Long productId;
    private BigDecimal price;
    private int quantity;
}
