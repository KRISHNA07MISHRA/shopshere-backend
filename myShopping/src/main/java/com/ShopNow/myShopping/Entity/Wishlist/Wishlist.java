
package com.ShopNow.myShopping.Entity.Wishlist;

import com.ShopNow.myShopping.Entity.AppUser;
import com.ShopNow.myShopping.Entity.Product;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(
    name = "wishlists",
    uniqueConstraints = {
        @UniqueConstraint(
            columnNames = {"user_id", "product_id"}
        )
    }
)
public class Wishlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    public Wishlist() {
    }
}
