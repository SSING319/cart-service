package org.app.cart.service;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.app.cart.DTO.AddToCartRequest;
import org.app.cart.DTO.ChangeQuantityRequest;
import org.app.cart.entitiy.Cart;
import org.app.cart.entitiy.CartItem;
import org.app.cart.enums.CartStatus;
import org.app.cart.events.AvailableQuantityEvent;
import org.app.cart.events.CheckQuantityEvent;
import org.app.cart.repository.CartItemRepository;
import org.app.cart.repository.CartRepository;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;


@ApplicationScoped
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    private static final Logger logger = LoggerFactory.getLogger(CartService.class);

    @Inject
    @Channel("check-quantity")
    Emitter<CheckQuantityEvent> checkQuantityEmitter;

    private final Map<Long, CompletableFuture<AvailableQuantityEvent>> pendingRequests = new ConcurrentHashMap<>();


    @Transactional
    public Uni<Void> addToCart(AddToCartRequest addToCartRequest){
        Cart cart = getCart(addToCartRequest);
        logger.info("cart created successfully with id: {}", cart.getId());
        CheckQuantityEvent checkQuantityEvent = CheckQuantityEvent.builder()
                .productId(addToCartRequest.getProductId())
                .requestedQuantity(addToCartRequest.getQuantity())
                .build();

        CompletableFuture<AvailableQuantityEvent> future = new CompletableFuture<>();
        pendingRequests.put(checkQuantityEvent.getProductId(), future);

        checkQuantityEmitter.send(checkQuantityEvent);

        return Uni.createFrom().completionStage(future)
                .onItem().invoke(event -> handleAvailableQuantityEvent(event, cart, addToCartRequest))
                .onFailure().invoke(throwable -> {
                    System.err.println("Failed to check quantity: " + throwable.getMessage());
                }).replaceWithVoid();

    }

    @Transactional
    public Uni<Void> changeQuantity(ChangeQuantityRequest changeQuantityRequest){
        Cart cart = getCart(changeQuantityRequest.getCartId());
        cartRepository.persist(cart);

        int countInCart = countAlreadyHave(cart, changeQuantityRequest.getProductId());

        int desiredQuantity = changeQuantityRequest.getQuantity() + countInCart;

        CheckQuantityEvent checkQuantityEvent = CheckQuantityEvent.builder()
                .productId(changeQuantityRequest.getProductId())
                .requestedQuantity(desiredQuantity)
                .build();

        CompletableFuture<AvailableQuantityEvent> future = new CompletableFuture<>();
        pendingRequests.put(checkQuantityEvent.getProductId(), future);

        checkQuantityEmitter.send(checkQuantityEvent);

        return Uni.createFrom().completionStage(future)
                .onItem().invoke(event -> handleChangeQuantityEvent(event, cart, checkQuantityEvent.getProductId(), desiredQuantity))
                .onFailure().invoke(throwable -> {
                    // Handle any failure
                    System.err.println("Failed to check quantity: " + throwable.getMessage());
                }).replaceWithVoid();

    }




    @Incoming("available-quantity")  // Listen for AvailableQuantityEvent messages
    public void consumeAvailableQuantity(AvailableQuantityEvent event) {
        CompletableFuture<AvailableQuantityEvent> future = pendingRequests.remove(event.getProductId());
        if (future != null) {
            future.complete(event); // Complete the future with the event
        }
    }

    private void handleChangeQuantityEvent(AvailableQuantityEvent event, Cart cart, Long productId, int desiredQuantity){
        if(event.isAvailable()){
            CartItem cartItem = cartItemRepository.findByCartAndProductId(cart, productId);
            cartItem.setQuantity(desiredQuantity);
            cartItemRepository.persist(cartItem);
            logger.info("product with productId: {} has been updated", productId);
        }
        else {
            logger.info("Only {} is in stock for this product_id: {}",event.getInStock(), event.getProductId());
        }
        pendingRequests.remove(event.getProductId());
    }


    private int countAlreadyHave(Cart cart, Long productId){
        CartItem cartItem = cartItemRepository.findByCartAndProductId(cart, productId);
        return cartItem.getQuantity();
    }

    private void handleAvailableQuantityEvent(AvailableQuantityEvent event, Cart cart, AddToCartRequest addToCartRequest) {
        if(event.isAvailable()){
            CartItem cartItem = CartItem.builder()
                    .cart(cart)
                    .productId(event.getProductId())
                    .quantity(addToCartRequest.getQuantity())
                    .price(addToCartRequest.getPrice())
                    .build();
            cartItemRepository.persist(cartItem);
            logger.info("product has been persisted to cartItem_db with product id: {}", event.getProductId());
        }
        else {
            logger.info("Only {} is in stock for this product_id: {}",event.getInStock(), event.getProductId());
        }
        pendingRequests.remove(event.getProductId());
    }

    private Cart getCart(AddToCartRequest addToCartRequest){
        Cart cart = cartRepository.findByUserId(addToCartRequest.getUserId());
        if(cart != null){
            if(cart.getStatus().equals(CartStatus.INACTIVE)){
                setCartStatusActive(cart);
            }
        }
        else {
            cart = createNewCart(addToCartRequest);
        }
        return cart;
    }

    private Cart getCart(Long cartId){
        return cartRepository.findById(cartId);
    }

    private void setCartStatusActive(Cart cart){
        cart.setStatus(CartStatus.ACTIVE);
        cartRepository.persist(cart);
    }

    private Cart createNewCart(AddToCartRequest addToCartRequest){
        Cart cart = Cart.builder()
                .userId(addToCartRequest.getUserId())
                .status(CartStatus.ACTIVE)
                .build();
        cartRepository.persist(cart);
        return cart;
    }
}
