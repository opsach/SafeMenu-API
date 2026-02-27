package com.safemenu.api.controller;

import com.safemenu.api.dto.request.IngredientRequest;
import com.safemenu.api.dto.response.IngredientResponse;
import com.safemenu.api.service.IngredientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ingredients")
@RequiredArgsConstructor
@Tag(name = "Ingredients", description = "Manage ingredients and their allergen profiles")
public class IngredientController {

    private final IngredientService ingredientService;

    @GetMapping
    @Operation(summary = "List all ingredients")
    public ResponseEntity<List<IngredientResponse>> findAll() {
        return ResponseEntity.ok(ingredientService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get ingredient by ID")
    public ResponseEntity<IngredientResponse> findById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(ingredientService.findById(id));
    }

    @GetMapping("/search")
    @Operation(summary = "Search ingredients by name")
    public ResponseEntity<List<IngredientResponse>> search(@RequestParam String query) {
        return ResponseEntity.ok(ingredientService.search(query));
    }

    @PostMapping
    @Operation(summary = "Create a new ingredient with allergen tags")
    public ResponseEntity<IngredientResponse> create(@Valid @RequestBody IngredientRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ingredientService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an ingredient")
    public ResponseEntity<IngredientResponse> update(
            @PathVariable("id") Long id,
            @Valid @RequestBody IngredientRequest request) {
        return ResponseEntity.ok(ingredientService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an ingredient")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        ingredientService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
