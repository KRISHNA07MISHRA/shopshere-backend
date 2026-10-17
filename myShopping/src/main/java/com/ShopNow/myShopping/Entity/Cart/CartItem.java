package com.ShopNow.myShopping.Entity.Cart;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

import com.ShopNow.myShopping.Entity.Product;
@Getter
@Setter
@Entity
@Table(
    name = "cart_items",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_cart_product",
            columnNames = {"cart_id", "product_id"}
        )
    }
)
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    // Price captured when the product is added to the cart
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;

    public CartItem() {
    }
}
