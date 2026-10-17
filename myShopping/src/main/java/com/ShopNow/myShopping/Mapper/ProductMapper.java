package com.ShopNow.myShopping.Mapper;

import org.springframework.stereotype.Component;

import com.ShopNow.myShopping.DTO.ProductPatchRequestDTO;
import com.ShopNow.myShopping.DTO.ProductRequestDTO;
import com.ShopNow.myShopping.DTO.ProductResponseDTO;
import com.ShopNow.myShopping.Entity.Product;

@Component 
public class ProductMapper {
    public Product toEntity(ProductRequestDTO request) {

        Product product = new Product();

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setCategory(request.getCategory());
        product.setBrand(request.getBrand());
        product.setImageUrl(request.getImageUrl());

        if (request.getActive() != null) {
            product.setActive(request.getActive());
        } else {
            product.setActive(false);
        }
        product.setTrending(request.getTrending());

        return product;
    }

    public void updateEntity(
            ProductRequestDTO request,
            Product product) {

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setCategory(request.getCategory());
        product.setBrand(request.getBrand());
        product.setImageUrl(request.getImageUrl());
        product.setActive(request.getActive());
        product.setTrending(request.getTrending());
    }

    public void patchEntity(
            ProductPatchRequestDTO request,
            Product product) {

        if (request.getName() != null) {
            product.setName(request.getName());
        }

        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }

        if (request.getPrice() != null) {
            product.setPrice(request.getPrice());
        }

        if (request.getStockQuantity() != null) {
            product.setStockQuantity(request.getStockQuantity());
        }

        if (request.getCategory() != null) {
            product.setCategory(request.getCategory());
        }

        if (request.getBrand() != null) {
            product.setBrand(request.getBrand());
        }

        if (request.getImageUrl() != null) {
            product.setImageUrl(request.getImageUrl());
        }

        if (request.getActive() != null) {
            product.setActive(request.getActive());
        }
        if(request.getTrending() != null){
            product.setTrending(request.getTrending());
        }
    }

    public ProductResponseDTO toResponse(Product product) {

        ProductResponseDTO response = new ProductResponseDTO();

        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setStockQuantity(product.getStockQuantity());
        response.setCategory(product.getCategory());
        response.setBrand(product.getBrand());
        response.setImageUrl(product.getImageUrl());
        response.setActive(product.getActive());
        response.setTrending(product.getTrending());

        return response;
    }
}
