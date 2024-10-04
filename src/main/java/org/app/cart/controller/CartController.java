package org.app.cart.controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.AllArgsConstructor;
import org.app.cart.DTO.AddToCartRequest;
import org.app.cart.DTO.ChangeQuantityRequest;
import org.app.cart.service.CartService;

@Path("/cart")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@AllArgsConstructor
public class CartController {

    @Inject
    private final CartService cartService;

    @Path("/add")
    @POST
    public Response addToCart(AddToCartRequest addToCartRequest){
        cartService.addToCart(addToCartRequest);
        return Response.status(Response.Status.ACCEPTED).entity("Items added to the cart").build();
    }

    @Path("/add/{cartId}")
    @POST
    public Response changeQuantity(@PathParam("cartId") Long cartId, AddToCartRequest addToCartRequest){
        ChangeQuantityRequest changeQuantityRequest = ChangeQuantityRequest.builder()
                .userId(addToCartRequest.getUserId())
                .cartId(cartId)
                .productId(addToCartRequest.getUserId())
                .quantity(addToCartRequest.getQuantity())
                .build();
        cartService.changeQuantity(changeQuantityRequest);
        return Response.status(Response.Status.ACCEPTED).entity("Item updated in the cart").build();
    }
}
