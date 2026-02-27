package com.safemenu.api.controller;

import com.safemenu.api.dto.request.CategoryRequest;
import com.safemenu.api.dto.response.CategoryResponse;
import com.safemenu.api.service.MenuCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Menu Categories", description = "Manage menu sections (Starters, Mains, Desserts, etc.)")
public class MenuCategoryController {

    private final MenuCategoryService categoryService;

    @GetMapping("/restaurant/{restaurantId}")
    @Operation(summary = "List categories for a restaurant (ordered by display order)")
    public ResponseEntity<List<CategoryResponse>> findByRestaurant(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(categoryService.findByRestaurant(restaurantId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category by ID")
    public ResponseEntity<CategoryResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new menu category")
    public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a menu category")
    public ResponseEntity<CategoryResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.ok(categoryService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a menu category and all its dishes")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
