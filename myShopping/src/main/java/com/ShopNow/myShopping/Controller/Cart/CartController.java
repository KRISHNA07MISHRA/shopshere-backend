package com.ShopNow.myShopping.Controller.Cart;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.ShopNow.myShopping.DTO.Cart.CartResponseDTO;
import com.ShopNow.myShopping.Entity.AppUser;
import com.ShopNow.myShopping.Repository.AppUserRepo;
import com.ShopNow.myShopping.Service.Cart.CartService;


@RestController 
@RequestMapping ("/cart")
public class CartController {
    
    private final CartService cartService;
    private final AppUserRepo appUserRepo;

    public CartController(
            CartService cartService,
            AppUserRepo appUserRepo) {
        this.cartService = cartService;
        this.appUserRepo = appUserRepo;
    }

    
    @GetMapping
    public CartResponseDTO getCart(Authentication authentication) {
        AppUser user = appUserRepo
                .findByEmail(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "User not found"));

        return cartService.getCartDetails(user);
    }

}
