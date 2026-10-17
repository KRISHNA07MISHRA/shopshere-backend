package com.ShopNow.myShopping.DTO.Login;

public record LoginResponse(

    Long id,
    String name,
    String email,
    String role,
    String token
) {

    
}
