package com.ShopNow.myShopping.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ShopNow.myShopping.Entity.AppUser;

public interface AppUserRepo extends JpaRepository<AppUser,Long>{
    Optional<AppUser> findByEmail(String email);

    boolean existsByEmail(String email);
}
