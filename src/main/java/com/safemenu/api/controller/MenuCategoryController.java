package com.safemenu.api.controller;

import com.safemenu.api.dto.request.CategoryRequest;
import com.safemenu.api.dto.response.ApiErrorResponse;
import com.safemenu.api.dto.response.CategoryResponse;
import com.safemenu.api.service.MenuCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @ApiResponse(responseCode = "200", description = "Categories returned")
    public ResponseEntity<List<CategoryResponse>> findByRestaurant(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(categoryService.findByRestaurant(restaurantId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category returned"),
            @ApiResponse(responseCode = "404", description = "Category not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<CategoryResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new menu category")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Category created"),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Restaurant not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a menu category")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category updated"),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Category or restaurant not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<CategoryResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.ok(categoryService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a menu category and all its dishes")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Category deleted"),
            @ApiResponse(responseCode = "404", description = "Category not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
