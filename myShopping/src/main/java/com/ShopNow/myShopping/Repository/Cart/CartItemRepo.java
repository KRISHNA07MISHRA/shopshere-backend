package com.ShopNow.myShopping.Repository.Cart;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ShopNow.myShopping.Entity.Product;
import com.ShopNow.myShopping.Entity.Cart.Cart;
import com.ShopNow.myShopping.Entity.Cart.CartItem;

public interface CartItemRepo extends JpaRepository<CartItem,Long>{
    List<CartItem> findByCartId(Long cartId);
    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);
    Optional<CartItem> findByIdAndCartId(Long id, Long cartId);
}
