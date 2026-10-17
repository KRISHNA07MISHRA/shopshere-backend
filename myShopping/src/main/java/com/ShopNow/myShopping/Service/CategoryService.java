
package com.ShopNow.myShopping.Service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.ShopNow.myShopping.Entity.Category;
import com.ShopNow.myShopping.Repository.CategoryRepo;

@Service
public class CategoryService {

    private final CategoryRepo repo;

    public CategoryService(CategoryRepo repo) {
        this.repo = repo;
    }

    public List<Category> getAllCategories() {
        return repo.findAll();
    }

    public Category addCategory(Category category) {
        return repo.save(category);
    }

    // PUT: Replace all editable category fields
    public Category updateCategory(Long id, Category updatedCategory) {
        Category existing = repo.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Category not found: " + id));

        existing.setName(updatedCategory.getName());
        existing.setImageUrl(updatedCategory.getImageUrl());
        existing.setActive(updatedCategory.isActive());

        return repo.save(existing);
    }

    // PATCH: Update only the fields provided in the request
    public Category patchCategory(Long id, Map<String, Object> updates) {
        Category existing = repo.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Category not found: " + id));

        if (updates.containsKey("name")) {
            Object value = updates.get("name");
            if (!(value instanceof String name) || name.isBlank()) {
                throw new IllegalArgumentException(
                        "name must be a non-empty string");
            }
            existing.setName(name);
        }

        if (updates.containsKey("imageUrl")) {
            Object value = updates.get("imageUrl");
            if (!(value instanceof String imageUrl)) {
                throw new IllegalArgumentException(
                        "imageUrl must be a string");
            }
            existing.setImageUrl(imageUrl);
        }

        if (updates.containsKey("active")) {
            Object value = updates.get("active");
            if (!(value instanceof Boolean active)) {
                throw new IllegalArgumentException(
                        "active must be true or false");
            }
            existing.setActive(active);
        }

        return repo.save(existing);
    }

    // DELETE: Remove category by ID
    public void deleteCategory(Long id) {
        Category existing = repo.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Category not found: " + id));

        repo.delete(existing);
    }
}
