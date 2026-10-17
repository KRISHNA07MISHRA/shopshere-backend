package com.ShopNow.myShopping.Repository.Wishlist;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ShopNow.myShopping.Entity.AppUser;
import com.ShopNow.myShopping.Entity.Product;
import com.ShopNow.myShopping.Entity.Wishlist.Wishlist;

public interface WishlistRepo extends JpaRepository<Wishlist,Long>{

    List<Wishlist> findByUser(AppUser user);
    Optional<Wishlist> findByUserAndProduct(AppUser user, Product product);
}
