package com.ShopNow.myShopping.Service;

import org.springframework.stereotype.Service;

import com.ShopNow.myShopping.DTO.ProductPatchRequestDTO;
import com.ShopNow.myShopping.DTO.ProductRequestDTO;
import com.ShopNow.myShopping.DTO.ProductResponseDTO;
import com.ShopNow.myShopping.Entity.Product;
import com.ShopNow.myShopping.Mapper.ProductMapper;
import com.ShopNow.myShopping.Repository.productRepo;

import jakarta.transaction.Transactional;

@Service 
public class ProductService {
    
    private final productRepo repository;
    private final ProductMapper mapper;

    // Constructor Injection
    public ProductService(productRepo repository,ProductMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    // CREATE
    @Transactional 
    public ProductResponseDTO save(ProductRequestDTO request) {
        Product product = mapper.toEntity(request);
        Product saveProduct = repository.save(product);
        return mapper.toResponse(saveProduct);
    }

    // READ
    @Transactional
    public ProductResponseDTO getProduct(Long id) {

        Product product = repository.findById(id)
                .orElseThrow(() ->
                    new RuntimeException()
                );

        return mapper.toResponse(product);
    }


    @Transactional
    public java.util.List<ProductResponseDTO> getAllProducts() {

        return repository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }
    // UPDATE
    @Transactional
    public ProductResponseDTO update(Long id, ProductRequestDTO request) {

        Product existingProduct = repository.findById(id)
            .orElseThrow(() ->
                    new RuntimeException(
                            "Product not found with id: " + id
                    )
            );

        mapper.updateEntity(request, existingProduct);
        Product savedProduct = repository.save(existingProduct);
        return mapper.toResponse(savedProduct);
    }

    // PATCH
    @Transactional
    public ProductResponseDTO patch(Long id, ProductPatchRequestDTO request) {

        Product existingProduct = repository.findById(id)
                .orElseThrow(()->
                new RuntimeException()
        );

       mapper.patchEntity(request, existingProduct);
       Product savedProduct = repository.save(existingProduct);
       return mapper.toResponse(savedProduct);
    }

    // DELETE
    @Transactional
    public void remove(Long id) {

        if (!repository.existsById(id)) {
            throw new RuntimeException(
                    "Product not found with id: " + id
            );
        }

        repository.deleteById(id);
    }

    
}
