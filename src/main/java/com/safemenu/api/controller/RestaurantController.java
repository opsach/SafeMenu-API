package com.safemenu.api.controller;

import com.safemenu.api.dto.request.RestaurantRequest;
import com.safemenu.api.dto.response.RestaurantResponse;
import com.safemenu.api.service.RestaurantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/restaurants")
@RequiredArgsConstructor
@Tag(name = "Restaurants", description = "Manage restaurant profiles")
public class RestaurantController {

    private final RestaurantService restaurantService;

    @GetMapping
    @Operation(summary = "List all restaurants")
    public ResponseEntity<List<RestaurantResponse>> findAll() {
        return ResponseEntity.ok(restaurantService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get restaurant by ID")
    public ResponseEntity<RestaurantResponse> findById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(restaurantService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new restaurant")
    public ResponseEntity<RestaurantResponse> create(@Valid @RequestBody RestaurantRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(restaurantService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing restaurant")
    public ResponseEntity<RestaurantResponse> update(
            @PathVariable("id") Long id,
            @Valid @RequestBody RestaurantRequest request) {
        return ResponseEntity.ok(restaurantService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a restaurant and all its menus")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        restaurantService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
