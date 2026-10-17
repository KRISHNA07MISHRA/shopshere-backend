package com.ShopNow.myShopping.DTO.Cart;

import java.math.BigDecimal;

public record CartItemResponseDTO(
    Long id,
    Long productId,
    String productName,
    String imageUrl,
    Integer quantity,
    BigDecimal unitPrice,
    BigDecimal subtotal
) {
    
}
