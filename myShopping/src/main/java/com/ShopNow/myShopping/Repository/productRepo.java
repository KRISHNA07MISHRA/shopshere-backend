package com.ShopNow.myShopping.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ShopNow.myShopping.Entity.Product;

@Repository 
public interface productRepo extends JpaRepository<Product,Long> {

    
} 