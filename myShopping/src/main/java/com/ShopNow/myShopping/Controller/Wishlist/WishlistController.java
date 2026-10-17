package com.ShopNow.myShopping.Controller.Wishlist;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.ShopNow.myShopping.Entity.AppUser;
import com.ShopNow.myShopping.Entity.Product;
import com.ShopNow.myShopping.Entity.Wishlist.Wishlist;
import com.ShopNow.myShopping.Repository.AppUserRepo;
import com.ShopNow.myShopping.Repository.productRepo;
import com.ShopNow.myShopping.Service.WishList.WishlistService;


@RestController 
@RequestMapping ("/wishlist")
public class WishlistController {
    private final WishlistService wishlistService;
    private final AppUserRepo appUserRepo;
    private final productRepo productRepo;

    public WishlistController(
            WishlistService wishlistService,
            AppUserRepo appUserRepo,
            productRepo productRepo) {
        this.wishlistService = wishlistService;
        this.appUserRepo = appUserRepo;
        this.productRepo = productRepo;
    }

    @GetMapping
    public List<Wishlist> getWishlist(Authentication authentication) {
        AppUser user = getCurrentUser(authentication);
        return wishlistService.getWishlist(user);
    }

    @PostMapping("/{productId}")
    public Wishlist addToWishlist(
            @PathVariable Long productId,
            Authentication authentication) {

        AppUser user = getCurrentUser(authentication);

        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Product not found"));

        return wishlistService.addToWishlist(user, product);
    }

    @DeleteMapping("/{productId}")
    public void removeFromWishlist(
            @PathVariable Long productId,
            Authentication authentication) {

        AppUser user = getCurrentUser(authentication);

        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Product not found"));

        wishlistService.removeFromWishlist(user, product);
    }

    private AppUser getCurrentUser(Authentication authentication) {
        return appUserRepo.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "User not found"));
    }
}
