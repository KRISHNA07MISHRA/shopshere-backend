package com.ShopNow.myShopping.DTO.Cart;

import java.math.BigDecimal;
import java.util.List;

public record CartResponseDTO(

    Long cartId,
        List<CartItemResponseDTO> items,
        Integer totalItems,
        BigDecimal totalAmount
) {

    
}
