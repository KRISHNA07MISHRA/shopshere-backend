package com.ShopNow.myShopping.Service.WishList;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ShopNow.myShopping.Entity.AppUser;
import com.ShopNow.myShopping.Entity.Product;
import com.ShopNow.myShopping.Entity.Wishlist.Wishlist;
import com.ShopNow.myShopping.Repository.Wishlist.WishlistRepo;

import jakarta.transaction.Transactional;

@Service 
public class WishlistService {
    private final WishlistRepo wishlistRepo;

    public WishlistService(WishlistRepo wishlistRepo) {
        this.wishlistRepo = wishlistRepo;
    }

    public List<Wishlist> getWishlist(AppUser user) {
        return wishlistRepo.findByUser(user);
    }

    @Transactional 
    public Wishlist addToWishlist(AppUser user, Product product) {
        return wishlistRepo.findByUserAndProduct(user, product)
                .orElseGet(() -> {
                    Wishlist wishlist = new Wishlist();
                    wishlist.setUser(user);
                    wishlist.setProduct(product);
                    return wishlistRepo.save(wishlist);
                });
    }

    public void removeFromWishlist(AppUser user, Product product) {
        Wishlist wishlist = wishlistRepo.findByUserAndProduct(user, product)
                .orElseThrow(() ->
                        new RuntimeException("Product not found in wishlist"));

        wishlistRepo.delete(wishlist);
    }
}
