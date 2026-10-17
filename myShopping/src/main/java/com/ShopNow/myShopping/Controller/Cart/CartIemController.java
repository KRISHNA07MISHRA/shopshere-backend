package com.ShopNow.myShopping.Controller.Cart;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.ShopNow.myShopping.DTO.Cart.CartItemResponseDTO;
import com.ShopNow.myShopping.Entity.AppUser;
import com.ShopNow.myShopping.Repository.AppUserRepo;
import com.ShopNow.myShopping.Service.Cart.CartItemService;

@RestController
@RequestMapping("/cart/items")
public class CartIemController {
    private final CartItemService cartItemService;
    private final AppUserRepo appUserRepo;

    public CartIemController(
            CartItemService cartItemService,
            AppUserRepo appUserRepo) {
        this.cartItemService = cartItemService;
        this.appUserRepo = appUserRepo;
    }

    private AppUser getAuthenticatedUser(Authentication authentication) {
        if (authentication == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Authentication required");
        }

        return appUserRepo.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "User not found"));
    }

    @GetMapping
    public List<CartItemResponseDTO> getItems(
            Authentication authentication) {
        return cartItemService.getItems(
                getAuthenticatedUser(authentication));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CartItemResponseDTO addItem(
            Authentication authentication,
            @RequestBody AddCartItemRequest request) {

        return cartItemService.addItem(
                getAuthenticatedUser(authentication),
                request.productId(),
                request.quantity());
    }

    @PatchMapping("/{itemId}")
    public CartItemResponseDTO updateQuantity(
            Authentication authentication,
            @PathVariable Long itemId,
            @RequestBody UpdateCartItemRequest request) {

        return cartItemService.updateQuantity(
                getAuthenticatedUser(authentication),
                itemId,
                request.quantity());
    }

    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeItem(
            Authentication authentication,
            @PathVariable Long itemId) {

        cartItemService.removeItem(
                getAuthenticatedUser(authentication), itemId);
    }

    public record AddCartItemRequest(Long productId, Integer quantity) {
    }

    public record UpdateCartItemRequest(Integer quantity) {
    }
}
