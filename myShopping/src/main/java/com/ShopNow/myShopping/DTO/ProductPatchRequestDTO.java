package com.ShopNow.myShopping.DTO;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter 
public class ProductPatchRequestDTO {
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;
    private String category;
    private String brand;
    private String imageUrl;
    private Boolean active;
    private Boolean trending;

    public ProductPatchRequestDTO() {
    }
}
