package com.ShopNow.myShopping.Repository.Cart;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ShopNow.myShopping.Entity.Cart.Cart;

import com.ShopNow.myShopping.Entity.AppUser;


public interface CartRepo extends JpaRepository<Cart,Long>{
    Optional<Cart> findByUser(AppUser user);
}
