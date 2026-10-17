package com.ShopNow.myShopping.Service.Cart;

import org.springframework.stereotype.Service;

import com.ShopNow.myShopping.DTO.Cart.CartItemResponseDTO;
import com.ShopNow.myShopping.DTO.Cart.CartResponseDTO;
import com.ShopNow.myShopping.Entity.AppUser;
import com.ShopNow.myShopping.Entity.Cart.Cart;
import com.ShopNow.myShopping.Repository.Cart.CartItemRepo;
import com.ShopNow.myShopping.Repository.Cart.CartRepo;
import org.springframework.transaction.annotation.Transactional;

@Service 
public class CartService {
    private final CartRepo cartRepo;
    private final CartItemRepo cartItemRepo;

    public CartService(CartRepo cartRepo,CartItemRepo cartItemRepo) {
        this.cartRepo = cartRepo;
        this.cartItemRepo = cartItemRepo;
    }

    @Transactional 
    public Cart getOrCreateCart(AppUser user) {
        return cartRepo.findByUser(user)
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setUser(user);
                    return cartRepo.save(cart);
                });
    }
    
    @Transactional
    public CartResponseDTO getCartDetails(AppUser user) {

        Cart cart = getOrCreateCart(user);

        java.util.List<CartItemResponseDTO> items =
                cartItemRepo.findByCartId(cart.getId())
                        .stream()
                        .map(item -> {
                            java.math.BigDecimal subtotal =
                                    item.getUnitPrice()
                                            .multiply(java.math.BigDecimal.valueOf(
                                                    item.getQuantity()));

                            return new CartItemResponseDTO(
                                    item.getId(),
                                    item.getProduct().getId(),
                                    item.getProduct().getName(),
                                    item.getProduct().getImageUrl(),
                                    item.getQuantity(),
                                    item.getUnitPrice(),
                                    subtotal
                            );
                        })
                        .toList();

        int totalItems = items.stream()
                .mapToInt(CartItemResponseDTO::quantity)
                .sum();

        java.math.BigDecimal totalAmount = items.stream()
                .map(CartItemResponseDTO::subtotal)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

        return new CartResponseDTO(
                cart.getId(),
                items,
                totalItems,
                totalAmount
        );
    }

}
