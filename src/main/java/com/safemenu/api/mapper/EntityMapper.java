package com.safemenu.api.mapper;

import com.safemenu.api.dto.response.*;
import com.safemenu.api.entity.*;
import com.safemenu.api.enums.AllergenType;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class EntityMapper {

    // ── Restaurant ──────────────────────────────────────────

    public RestaurantResponse toRestaurantResponse(Restaurant entity) {
        int dishCount = entity.getCategories().stream()
                .mapToInt(c -> c.getDishes().size())
                .sum();

        return RestaurantResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .address(entity.getAddress())
                .phone(entity.getPhone())
                .email(entity.getEmail())
                .description(entity.getDescription())
                .active(entity.isActive())
                .categoryCount(entity.getCategories().size())
                .dishCount(dishCount)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    // ── Category ────────────────────────────────────────────

    public CategoryResponse toCategoryResponse(MenuCategory entity) {
        return CategoryResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .displayOrder(entity.getDisplayOrder())
                .restaurantId(entity.getRestaurant().getId())
                .restaurantName(entity.getRestaurant().getName())
                .dishCount(entity.getDishes().size())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    // ── Ingredient ──────────────────────────────────────────

    public IngredientResponse toIngredientResponse(Ingredient entity) {
        return IngredientResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .allergens(entity.getAllergens())
                .build();
    }

    // ── Dish ────────────────────────────────────────────────

    public DishResponse toDishResponse(Dish entity) {
        Set<AllergenType> allergens = entity.getComputedAllergens();

        Set<IngredientResponse> ingredientResponses = entity.getIngredients().stream()
                .map(this::toIngredientResponse)
                .collect(Collectors.toSet());

        return DishResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .available(entity.isAvailable())
                .vegetarian(entity.isVegetarian())
                .vegan(entity.isVegan())
                .categoryName(entity.getCategory().getName())
                .categoryId(entity.getCategory().getId())
                .ingredients(ingredientResponses)
                .allergens(allergens)
                .allergenWarning(buildAllergenWarning(allergens))
                .build();
    }

    /**
     * Generates a human-readable allergen warning string.
     * E.g. "⚠ Contains: Milk, Eggs, Cereals containing gluten"
     */
    private String buildAllergenWarning(Set<AllergenType> allergens) {
        if (allergens.isEmpty()) {
            return "No known allergens";
        }
        String list = allergens.stream()
                .map(AllergenType::getDisplayName)
                .sorted()
                .collect(Collectors.joining(", "));
        return "⚠ Contains: " + list;
    }
}
