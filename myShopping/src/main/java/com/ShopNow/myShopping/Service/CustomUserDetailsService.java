package com.ShopNow.myShopping.Service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ShopNow.myShopping.Entity.AppUser;
import com.ShopNow.myShopping.Repository.AppUserRepo;

@Service 
public class CustomUserDetailsService implements UserDetailsService{
    private final AppUserRepo appUserRepo;

    public CustomUserDetailsService(AppUserRepo appUserRepo) {
        this.appUserRepo = appUserRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        AppUser appUser = appUserRepo.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found"));

        return User.withUsername(appUser.getEmail())
                .password(appUser.getPasswordHash())
                .authorities(appUser.getRole().name())
                .disabled(!appUser.isActive())
                .build();
    }
}
