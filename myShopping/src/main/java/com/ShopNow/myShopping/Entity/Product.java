package com.ShopNow.myShopping.Entity;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
@Entity  
public class Product {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;
    private String category;
    private String brand;
    private String imageUrl;
    private Boolean active;
    private Boolean trending;
    
    public Product(){}

    public Product(Long Id, String Name, String Description, BigDecimal Price, int StockQuantity,
        String Category, String Brand, String ImageUrl, boolean Active, Boolean trending
    ){
        this.id = Id;
        this.name = Name;
        this.description = Description;
        this.price = Price;
        this.stockQuantity = StockQuantity;
        this.category = Category;
        this.brand = Brand;
        this.imageUrl = ImageUrl;
        this.active = Active;
        this.trending = trending;
    }
}
