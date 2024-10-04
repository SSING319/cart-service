package org.app.cart.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.app.cart.entitiy.Cart;
import org.app.cart.entitiy.CartItem;

import java.util.List;

@ApplicationScoped
public class CartItemRepository implements PanacheRepository<CartItem> {
    public CartItem findByCartAndProductId(Cart cart, Long productId) {
        return  find("cart = ?1 and productId = ?2", cart, productId).firstResult();
    }
}
