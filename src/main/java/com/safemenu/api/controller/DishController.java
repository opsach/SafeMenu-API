package com.safemenu.api.controller;

import com.safemenu.api.dto.request.DishRequest;
import com.safemenu.api.dto.response.DishResponse;
import com.safemenu.api.enums.AllergenType;
import com.safemenu.api.service.DishService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/dishes")
@RequiredArgsConstructor
@Tag(name = "Dishes", description = "Manage dishes with auto-computed allergen detection")
public class DishController {

    private final DishService dishService;

    @GetMapping("/{id}")
    @Operation(summary = "Get dish by ID with full allergen breakdown")
    public ResponseEntity<DishResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(dishService.findById(id));
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "List dishes in a menu category")
    public ResponseEntity<List<DishResponse>> findByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(dishService.findByCategory(categoryId));
    }

    @GetMapping("/restaurant/{restaurantId}")
    @Operation(summary = "Paginated full menu for a restaurant")
    public ResponseEntity<Page<DishResponse>> findByRestaurant(
            @PathVariable Long restaurantId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(dishService.findByRestaurant(restaurantId, pageable));
    }

    /**
     * 🛡️ CORE FEATURE — Allergen-safe dish finder.
     *
     * Customers with allergies can specify which allergens to avoid,
     * and the API returns only dishes that are safe for them.
     *
     * Example: GET /api/v1/dishes/safe?restaurantId=1&exclude=MILK,NUTS,EGGS
     */
    @GetMapping("/safe")
    @Operation(summary = "Find dishes safe for specific allergies (excludes dishes containing specified allergens)")
    public ResponseEntity<List<DishResponse>> findSafeDishes(
            @RequestParam Long restaurantId,
            @RequestParam Set<AllergenType> exclude) {
        return ResponseEntity.ok(dishService.findSafeDishes(restaurantId, exclude));
    }

    @GetMapping("/restaurant/{restaurantId}/vegetarian")
    @Operation(summary = "List vegetarian dishes for a restaurant")
    public ResponseEntity<List<DishResponse>> findVegetarian(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(dishService.findVegetarian(restaurantId));
    }

    @GetMapping("/restaurant/{restaurantId}/vegan")
    @Operation(summary = "List vegan dishes for a restaurant")
    public ResponseEntity<List<DishResponse>> findVegan(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(dishService.findVegan(restaurantId));
    }

    @PostMapping
    @Operation(summary = "Create a dish — allergens are auto-computed from ingredients")
    public ResponseEntity<DishResponse> create(@Valid @RequestBody DishRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(dishService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a dish and recompute allergens")
    public ResponseEntity<DishResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody DishRequest request) {
        return ResponseEntity.ok(dishService.update(id, request));
    }

    @PatchMapping("/{id}/toggle-availability")
    @Operation(summary = "Toggle dish availability (86'd / back on)")
    public ResponseEntity<DishResponse> toggleAvailability(@PathVariable Long id) {
        return ResponseEntity.ok(dishService.toggleAvailability(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a dish")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        dishService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
