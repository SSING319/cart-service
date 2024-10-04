package org.app.cart.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.app.cart.entitiy.Cart;
@ApplicationScoped
public class CartRepository implements PanacheRepository<Cart> {
    public Cart findByUserId(Long userId) {
        return find("userId", userId).firstResult();
    }
}
