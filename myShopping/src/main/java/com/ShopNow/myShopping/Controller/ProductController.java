package com.ShopNow.myShopping.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ShopNow.myShopping.DTO.ProductPatchRequestDTO;
import com.ShopNow.myShopping.DTO.ProductRequestDTO;
import com.ShopNow.myShopping.DTO.ProductResponseDTO;
import com.ShopNow.myShopping.Entity.Product;
import com.ShopNow.myShopping.Service.ProductService;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController 
@RequestMapping ("/product")
public class ProductController {
    
    private final ProductService service;

    public ProductController(ProductService service){
        this.service = service;
    }

    @GetMapping
    public List<ProductResponseDTO> getAllProducts() {
        return service.getAllProducts();
    }
    

    @GetMapping("/{id}") // product ko Id se lena
    public ProductResponseDTO getProduct(@PathVariable  Long id) {
        return service.getProduct(id);
    }

    @PostMapping
    public ProductResponseDTO addProduct(@RequestBody ProductRequestDTO request) {
        return service.save(request);
    }

    @PutMapping("/{id}")
    public ProductResponseDTO updateProduct(@PathVariable Long id, @RequestBody ProductRequestDTO p) {
        return service.update(id, p);
    }

    @PatchMapping("/{id}")
    public ProductResponseDTO patchProduct(
            @PathVariable Long id,
            @RequestBody ProductPatchRequestDTO p) {
        return service.patch(id, p);
    }

    @DeleteMapping ("/{id}")
    public void deleteProduct(@PathVariable Long id){
        service.remove(id);
    }
}



// @RequestBody -> Request ke JSON body ko Java object mein convert karta hai

// @RequestParam -> URL ke query parameter ke liye

// @pathvariable -> URL ke path ke andar value ke liye

