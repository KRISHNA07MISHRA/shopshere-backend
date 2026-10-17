package com.ShopNow.myShopping.Service.Cart;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.ShopNow.myShopping.DTO.Cart.CartItemResponseDTO;
import com.ShopNow.myShopping.Entity.AppUser;
import com.ShopNow.myShopping.Entity.Product;
import com.ShopNow.myShopping.Entity.Cart.Cart;
import com.ShopNow.myShopping.Entity.Cart.CartItem;
import com.ShopNow.myShopping.Repository.productRepo;
import com.ShopNow.myShopping.Repository.Cart.CartItemRepo;
import com.ShopNow.myShopping.Repository.Cart.CartRepo;

import jakarta.transaction.Transactional;

@Service 
public class CartItemService {
    private final CartItemRepo cartItemRepo;
    private final CartService cartService;
    private final productRepo productRepo;

    public CartItemService(
            CartItemRepo cartItemRepo,
            CartService cartService,
            productRepo productRepo) {
        this.cartItemRepo = cartItemRepo;
        this.cartService = cartService;
        this.productRepo = productRepo;
    }

    private Cart getUserCart(AppUser user) {
        return cartService.getOrCreateCart(user);
    }

    private Product getAvailableProduct(Long productId) {
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Product not found"));

        if (!Boolean.TRUE.equals(product.getActive())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Product is inactive");
        }

        if (product.getStockQuantity() == null ||
                product.getStockQuantity() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Product is out of stock");
        }

        return product;
    }

    @Transactional
    public List<CartItemResponseDTO> getItems(AppUser user) {
        Cart cart = getUserCart(user);

        return cartItemRepo.findByCartId(cart.getId())
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional
    public CartItemResponseDTO addItem(
            AppUser user, Long productId, Integer quantity) {

        if (quantity == null || quantity <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Quantity must be greater than zero");
        }

        Cart cart = getUserCart(user);
        Product product = getAvailableProduct(productId);

        CartItem item = cartItemRepo
                .findByCartAndProduct(cart, product)
                .orElse(null);

        int newQuantity = quantity;

        if (item != null) {
            newQuantity = item.getQuantity() + quantity;
        } else {
            item = new CartItem();
            item.setCart(cart);
            item.setProduct(product);
            item.setUnitPrice(product.getPrice());
        }

        if (newQuantity > product.getStockQuantity()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Requested quantity exceeds available stock");
        }

        item.setQuantity(newQuantity);
        item.setUnitPrice(product.getPrice());

        return toDTO(cartItemRepo.save(item));
    }

    @Transactional
    public CartItemResponseDTO updateQuantity(
            AppUser user, Long itemId, Integer quantity) {

        if (quantity == null || quantity <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Quantity must be greater than zero");
        }

        Cart cart = getUserCart(user);

        CartItem item = cartItemRepo
                .findByIdAndCartId(itemId, cart.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Cart item not found"));

        Product product = getAvailableProduct(item.getProduct().getId());

        if (quantity > product.getStockQuantity()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Requested quantity exceeds available stock");
        }

        item.setQuantity(quantity);
        item.setUnitPrice(product.getPrice());

        return toDTO(cartItemRepo.save(item));
    }

    @Transactional
    public void removeItem(AppUser user, Long itemId) {
        Cart cart = getUserCart(user);

        CartItem item = cartItemRepo
                .findByIdAndCartId(itemId, cart.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Cart item not found"));

        cartItemRepo.delete(item);
    }

    private CartItemResponseDTO toDTO(CartItem item) {
        Product product = item.getProduct();

        BigDecimal subtotal = item.getUnitPrice()
                .multiply(BigDecimal.valueOf(item.getQuantity()));

        return new CartItemResponseDTO(
                item.getId(),
                product.getId(),
                product.getName(),
                product.getImageUrl(),
                item.getQuantity(),
                item.getUnitPrice(),
                subtotal
        );
    }
}
